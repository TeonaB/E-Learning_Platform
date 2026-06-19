package com.elearning.platform.repository;

import com.elearning.platform.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);

    Page<Category> findAll(Pageable pageable);

    @Query("SELECT c FROM Category c LEFT JOIN c.courses co GROUP BY c.id ORDER BY COUNT(co) DESC")
    Page<Category> findAllSortedByCoursesCountDesc(Pageable pageable);

    @Query("SELECT c FROM Category c LEFT JOIN c.courses co GROUP BY c.id ORDER BY COUNT(co) ASC")
    Page<Category> findAllSortedByCoursesCountAsc(Pageable pageable);
}