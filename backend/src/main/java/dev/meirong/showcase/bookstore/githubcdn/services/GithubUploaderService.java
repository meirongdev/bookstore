package dev.meirong.showcase.bookstore.githubcdn.services;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.meirong.showcase.bookstore.githubcdn.config.GithubCdnProperties;
import dev.meirong.showcase.bookstore.utils.KeyGenerator;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@RequiredArgsConstructor
public class GithubUploaderService {

  private final GithubCdnProperties properties;

  private static final String GITHUB_API_TEMPLATE = "https://api.github.com/repos/%s/%s/contents/%s";
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  private final OkHttpClient client = new OkHttpClient();
  private final ObjectMapper objectMapper = new ObjectMapper();


  /**
   * 上传文件到 GitHub 并返回 jsDelivr CDN 链接
   */
  public String upload(byte[] bytes, String originalFilename, String destDir) throws IOException {
    String filename = KeyGenerator.next() + "_" + originalFilename;
    String separator = java.io.File.separator;
    String path = (destDir == null || destDir.isBlank() ? properties.basePath() : (properties.basePath() + separator + destDir)).replaceAll("(^/)|(/$)", "")
        + separator + filename;
    path = path.replaceAll("^" + separator, "");

    String apiUrl = String.format(GITHUB_API_TEMPLATE, properties.owner(), properties.repo(), path);

    String base64Content = Base64.getEncoder().encodeToString(bytes);

    Map<String, String> payload = Map.of(
        "message", "upload via spring-boot-gh-cdn",
        "branch", properties.branch(),
        "content", base64Content);

    String json = objectMapper.writeValueAsString(payload);

    RequestBody body = RequestBody.create(json, JSON);
    Request request = new Request.Builder()
        .url(apiUrl)
        .header("Authorization", "Bearer " + properties.token())
        .header("Accept", "application/vnd.github.v3+json")
        .put(body)
        .build();

    try (Response resp = client.newCall(request).execute()) {
      if (!resp.isSuccessful()) {
        String respBody = resp.body().string();
        throw new IOException("upload failed: " + resp.code() + " " + resp.message() + " - " + respBody);
      }
    }

    // 拼接 jsDelivr 链接
    return String.format("https://cdn.jsdelivr.net/gh/%s/%s@%s/%s", properties.owner(), properties.repo(), properties.branch(), path);
  }
}
