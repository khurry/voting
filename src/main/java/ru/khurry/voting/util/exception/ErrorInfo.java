package ru.khurry.voting.util.exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ErrorInfo {
    private final String url;
    private final String details;

    public ErrorInfo(String url, String details) {
        this.url = url;
        this.details = details;
    }

    public String getUrl() {
        return url;
    }

    public String getDetails() {
        return details;
    }
}