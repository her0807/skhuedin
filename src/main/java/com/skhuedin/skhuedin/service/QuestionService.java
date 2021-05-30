package com.skhuedin.skhuedin.service;

import com.skhuedin.skhuedin.domain.Question;
import com.skhuedin.skhuedin.domain.User;
import com.skhuedin.skhuedin.dto.comment.CommentMainResponseDto;
import com.skhuedin.skhuedin.dto.posts.PostsMainResponseDto;
import com.skhuedin.skhuedin.dto.question.QuestionMainResponseDto;
import com.skhuedin.skhuedin.dto.question.QuestionSaveRequestDto;
import com.skhuedin.skhuedin.infra.Role;
import com.skhuedin.skhuedin.repository.QuestionRepository;
import com.skhuedin.skhuedin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;

    @Transactional
    public Long save(QuestionSaveRequestDto requestDto) {
        User targetUser = getUser(requestDto.getTargetUserId());
        User writerUser = getUser(requestDto.getWriterUserId());

        return questionRepository.save(requestDto.toEntity(targetUser, writerUser)).getId();
    }

    @Transactional
    public Long update(Long id, QuestionSaveRequestDto requestDto) {
        User targetUser = getUser(requestDto.getTargetUserId());
        User writerUser = getUser(requestDto.getWriterUserId());

        Question question = getQuestion(id);
        question.updateQuestion(requestDto.toEntity(targetUser, writerUser));

        return question.getId();
    }

    @Transactional
    public void updateStatus(Long id) {
        Question question = getQuestion(id);

        if (question.getStatus() == false) {
            question.updateStatus(true);
        } else if (question.getStatus() == true) {
            question.updateStatus(false);
        }
    }

    @Transactional
    public void delete(Long id) {
        Question question = getQuestion(id);
        questionRepository.delete(question);
    }

    public QuestionMainResponseDto findById(Long id) {
        Question question = getQuestion(id);
        List<CommentMainResponseDto> comments = commentService.findByQuestionId(question.getId());
        return new QuestionMainResponseDto(question, comments);
    }

    public List<QuestionMainResponseDto> findAll() {
        return questionRepository.findAll().stream()
                .map(question -> new QuestionMainResponseDto(question)).collect(Collectors.toList());
    }

    public List<QuestionMainResponseDto> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).stream()
                .map(question -> new QuestionMainResponseDto(question)).collect(Collectors.toList());
    }

    public Page<QuestionMainResponseDto> findByTargetUserId(Long id, Pageable pageable) {
        Page<Question> questions = questionRepository.findByTargetUserId(id, pageable);
        return questions
                .map(question -> {
                    List<CommentMainResponseDto> comments = commentService.findByQuestionId(question.getId());
                    return new QuestionMainResponseDto(question, comments);
                });
    }

    @Transactional
    public void addView(Long id) {
        Question question = getQuestion(id);
        question.addView();
    }

    private Question getQuestion(Long id) {
        return questionRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 question 이 존재하지 않습니다. id=" + id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 user 가 존재하지 않습니다. id=" + id));
    }
}