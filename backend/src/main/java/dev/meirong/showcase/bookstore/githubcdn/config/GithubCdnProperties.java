package dev.meirong.showcase.bookstore.githubcdn.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for GitHub CDN integration.
 * Binds to properties under the prefix "app.github".
 *
 * @param owner The owner of the GitHub repository.
 * @param repo The name of the GitHub repository.
 * @param branch The branch to which files will be uploaded.
 * @param basePath The base directory path within the repository for uploads.
 * @param token The name of the environment variable holding the GitHub useral access token.
 * @param maxFileSizeBytes The maximum allowed file size for uploads, in bytes.
 */
@ConfigurationProperties(prefix = "app.github")
public record GithubCdnProperties(
    String owner,
    String repo,
    String branch,
    String basePath,
    String token,
    long maxFileSizeBytes
) {}
