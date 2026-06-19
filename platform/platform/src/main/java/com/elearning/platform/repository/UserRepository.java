package com.elearning.platform.repository;

import com.elearning.platform.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN u.courses c GROUP BY u.id ORDER BY COUNT(c) DESC")
    Page<User> findAllSortedByCoursesCountDesc(Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN u.courses c GROUP BY u.id ORDER BY COUNT(c) ASC")
    Page<User> findAllSortedByCoursesCountAsc(Pageable pageable);
}