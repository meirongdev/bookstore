package dev.meirong.showcase.bookstore.metrics;

import java.io.IOException;
import java.util.Optional;

import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Component
public class GitCommitMetrics {

  public GitCommitMetrics(MeterRegistry registry, Optional<GitProperties> gitProperties) throws IOException {
    String commitId = gitProperties
        .map(GitProperties::getShortCommitId)
        .filter(id -> id != null && !id.isEmpty())
        .orElse("unknown");

    registry.gauge("app_git_commit_info",
        Tags.of("commit", commitId), 1);
  }
}
