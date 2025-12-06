package dev.meirong.showcase.bookstore.githubcdn.services;

public class GithubException extends RuntimeException {
    private final int status;

    public GithubException(int status, String body) {
        super("GitHub API error: " + status + " - " + body);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
