package com.skhuedin.skhuedin.repository;

import com.skhuedin.skhuedin.domain.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"question" , "writerUser", "parent"})
    @Query("select c from Comment c where c.question.id = :questionId and c.parent is null")
    List<Comment> findByQuestionId(@Param("questionId") Long questionId);

    @EntityGraph(attributePaths = {"question" , "writerUser", "parent"})
    List<Comment> findByParentId(Long parentId);
}