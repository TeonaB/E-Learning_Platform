package com.elearning.platform.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "content_url", nullable = false)
    private String contentUrl;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}