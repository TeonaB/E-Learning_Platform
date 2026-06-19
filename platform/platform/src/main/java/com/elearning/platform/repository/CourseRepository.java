package com.elearning.platform.repository;

import com.elearning.platform.domain.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCategoryId(Long categoryId);

    List<Course> findByUsers_Id(Long userId);

    Page<Course> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Course> findAll(Pageable pageable);

    @Query("SELECT c FROM Course c LEFT JOIN c.reviews r GROUP BY c.id ORDER BY COALESCE(AVG(r.rating), 0.0) DESC")
    Page<Course> findAllSortedByAverageRatingDesc(Pageable pageable);

    @Query("SELECT c FROM Course c LEFT JOIN c.reviews r GROUP BY c.id ORDER BY COALESCE(AVG(r.rating), 0.0) ASC")
    Page<Course> findAllSortedByAverageRatingAsc(Pageable pageable);

    @Query("SELECT c FROM Course c LEFT JOIN c.reviews r WHERE c.category.id = :categoryId GROUP BY c.id ORDER BY COALESCE(AVG(r.rating), 0.0) DESC")
    Page<Course> findByCategoryIdSortedByAverageRatingDesc(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT c FROM Course c LEFT JOIN c.reviews r WHERE c.category.id = :categoryId GROUP BY c.id ORDER BY COALESCE(AVG(r.rating), 0.0) ASC")
    Page<Course> findByCategoryIdSortedByAverageRatingAsc(@Param("categoryId") Long categoryId, Pageable pageable);
}