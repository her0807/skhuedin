package com.skhuedin.skhuedin.repository;

import com.skhuedin.skhuedin.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @EntityGraph(attributePaths = {"writerUser"})
    Page<Question> findByTargetUserId(Long id, Pageable pageable);
}