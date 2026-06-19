package com.elearning.platform.mapper;

import com.elearning.platform.domain.Category;
import com.elearning.platform.dto.CategoryDto;
import com.elearning.platform.dto.CategoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    private final ModelMapper modelMapper;

    public CategoryResponseDto toCategoryResponseDto(Category category) {
        if (category == null) {
            return null;
        }
        return modelMapper.map(category, CategoryResponseDto.class);
    }

    public CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }
        return modelMapper.map(category, CategoryDto.class);
    }

    public Category toCategory(CategoryDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Category.class);
    }
}
