package com.elearning.platform.controller;

import com.elearning.platform.domain.Lesson;
import com.elearning.platform.dto.LessonDto;
import com.elearning.platform.dto.LessonResponseDto;
import com.elearning.platform.mapper.ResponseMapper;
import com.elearning.platform.service.interf.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
@Validated
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public ResponseEntity<List<LessonResponseDto>> getAll() {
        return ResponseEntity.ok(lessonService.getAllLessons().stream().map(ResponseMapper::toLessonDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonResponseDto> getById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ResponseMapper.toLessonDto(lessonService.getLessonById(id)));
    }

    @PostMapping("/{courseId}")
    public ResponseEntity<LessonResponseDto> create(@PathVariable @Positive Long courseId,
                                                    @Valid @RequestBody LessonDto lessonDto) {
        Lesson lesson = new Lesson();
        lesson.setTitle(lessonDto.getTitle());
        lesson.setContentUrl(lessonDto.getContentUrl());
        lesson.setDurationMinutes(lessonDto.getDurationMinutes());

        Lesson saved = lessonService.createLesson(courseId, lesson);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(ResponseMapper.toLessonDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonResponseDto> update(@PathVariable @Positive Long id,
                                                    @Valid @RequestBody LessonDto lessonDto) {
        Lesson lesson = new Lesson();
        lesson.setTitle(lessonDto.getTitle());
        lesson.setContentUrl(lessonDto.getContentUrl());
        lesson.setDurationMinutes(lessonDto.getDurationMinutes());

        Lesson updated = lessonService.updateLesson(id, lesson);
        return ResponseEntity.ok(ResponseMapper.toLessonDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonResponseDto>> getByCourse(@PathVariable @Positive Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId).stream().map(ResponseMapper::toLessonDto).toList());
    }
}