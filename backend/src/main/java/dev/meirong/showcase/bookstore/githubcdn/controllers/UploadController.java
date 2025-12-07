package dev.meirong.showcase.bookstore.githubcdn.controllers;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import dev.meirong.showcase.bookstore.githubcdn.dto.UploadResponse;
import dev.meirong.showcase.bookstore.githubcdn.services.GithubUploaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/upload/secure")
@Tag(name = "Upload Controller")
@RequiredArgsConstructor
public class UploadController {

  private final GithubUploaderService uploader;

  @Operation(summary = "Upload file to GitHub CDN.", description = "Uploads a file to GitHub and returns the jsDelivr CDN URL.")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public UploadResponse upload(@RequestPart("file") MultipartFile file,
      @RequestParam(required = false) String dir) {
    if (file == null || file.isEmpty()) {
      return new UploadResponse(false, null, "empty file");
    }

    String originalFilename = file.getOriginalFilename();
    String original = originalFilename != null ? StringUtils.cleanPath(originalFilename) : "";
    try {
      String cdn = uploader.upload(file.getBytes(), original, dir);
      return new UploadResponse(true, cdn, "ok");
    } catch (IOException e) {
      return new UploadResponse(false, null, "upload failed: " + e.getMessage());
    }
  }
}
