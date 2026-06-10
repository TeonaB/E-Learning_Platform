package com.elearning.platform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class LessonDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content URL is required")
    private String contentUrl;

    @Min(value = 0, message = "Duration must be >= 0")
    private Integer durationMinutes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
