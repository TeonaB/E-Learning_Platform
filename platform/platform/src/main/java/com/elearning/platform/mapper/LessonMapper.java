package com.elearning.platform.mapper;

import com.elearning.platform.domain.Lesson;
import com.elearning.platform.dto.LessonDto;
import com.elearning.platform.dto.LessonResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LessonMapper {

    private final ModelMapper modelMapper;

    public LessonResponseDto toLessonResponseDto(Lesson lesson) {
        if (lesson == null) {
            return null;
        }
        LessonResponseDto dto = modelMapper.map(lesson, LessonResponseDto.class);
        dto.setCourseId(lesson.getCourse() != null ? lesson.getCourse().getId() : null);
        return dto;
    }

    public LessonDto toLessonDto(Lesson lesson) {
        if (lesson == null) {
            return null;
        }
        return modelMapper.map(lesson, LessonDto.class);
    }

    public Lesson toLesson(LessonDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Lesson.class);
    }
}
