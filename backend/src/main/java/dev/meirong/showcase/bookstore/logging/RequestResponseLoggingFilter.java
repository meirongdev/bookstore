package dev.meirong.showcase.bookstore.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

  private static final int MAX_PAYLOAD_LENGTH = 4096;
  private static final String MDC_REQUEST_ID = "requestId";

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    // 只记录 /api 下的请求；如需全部请求改为 false
    return !path.startsWith("/api");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);
    ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

    // Only set MDC if X-Request-ID header is present and not blank
    String requestId = request.getHeader("X-Request-ID");
    if (requestId != null && !requestId.isBlank()) {
      MDC.put(MDC_REQUEST_ID, requestId);
    }

    long start = System.currentTimeMillis();
    try {
      filterChain.doFilter(wrappedRequest, wrappedResponse);
    } finally {
      long duration = System.currentTimeMillis() - start;
      try {
        logRequest(wrappedRequest);
        logResponse(wrappedResponse, duration);
      } catch (Exception e) {
        log.warn("Failed to log request/response", e);
      } finally {
        MDC.remove(MDC_REQUEST_ID); // Safe to remove even if not set
        wrappedResponse.copyBodyToResponse();
      }
    }
  }

  // Removed getOrCreateRequestId method as it's no longer needed

  // trace/span extraction is delegated to micrometer tracing bridge when
  // available; helpers removed.

  private void logRequest(ContentCachingRequestWrapper request) {
    String method = request.getMethod();
    String uri = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
    Map<String, String> headers = getHeaders(request);
    String requestBody = payloadAsString(request.getContentAsByteArray(), request.getCharacterEncoding());
    log.info("Request: method={}, uri={}, headers={}, payload={}", method, uri, headers, requestBody);
  }

  private void logResponse(ContentCachingResponseWrapper response, long duration) {
    int status = response.getStatus();
    Map<String, String> headers = getHeaders(response);
    String responseBody = payloadAsString(response.getContentAsByteArray(), response.getCharacterEncoding());
    log.info("Response({} ms): status={}, headers={}, payload={}", duration, status, headers, responseBody);
  }

  private Map<String, String> getHeaders(HttpServletRequest request) {
    Map<String, String> headers = new HashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      headers.put(headerName, request.getHeader(headerName));
    }
    return headers;
  }

  private Map<String, String> getHeaders(ContentCachingResponseWrapper response) {
    Map<String, String> headers = new HashMap<>();
    response.getHeaderNames().forEach(headerName -> headers.put(headerName, response.getHeader(headerName)));
    return headers;
  }

  private String payloadAsString(byte[] buf, String encoding) {
    if (buf == null || buf.length == 0)
      return "";
    int len = Math.min(buf.length, MAX_PAYLOAD_LENGTH);
    try {
      return new String(buf, 0, len, encoding != null ? encoding : StandardCharsets.UTF_8.name());
    } catch (Exception e) {
      return "[unknown]";
    }
  }

  // Header masking/formatting delegated to logback configuration
  // (logback-spring.xml
}
