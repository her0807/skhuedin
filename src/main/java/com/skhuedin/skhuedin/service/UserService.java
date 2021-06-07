package com.skhuedin.skhuedin.service;

import com.skhuedin.skhuedin.domain.Blog;
import com.skhuedin.skhuedin.domain.Comment;
import com.skhuedin.skhuedin.domain.Posts;
import com.skhuedin.skhuedin.domain.Question;
import com.skhuedin.skhuedin.domain.User;

import com.skhuedin.skhuedin.dto.user.UserMainResponseDto;
import com.skhuedin.skhuedin.dto.user.UserSaveRequestDto;
import com.skhuedin.skhuedin.dto.user.UserUpdateDto;
import com.skhuedin.skhuedin.infra.JwtTokenProvider;
import com.skhuedin.skhuedin.infra.LoginRequest;

import com.skhuedin.skhuedin.infra.Role;
import com.skhuedin.skhuedin.repository.BlogRepository;
import com.skhuedin.skhuedin.repository.CommentRepository;
import com.skhuedin.skhuedin.repository.PostsRepository;
import com.skhuedin.skhuedin.repository.QuestionRepository;
import com.skhuedin.skhuedin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BlogRepository blogRepository;
    private final PostsRepository postsRepository;
    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public Long save(User user) {
        return userRepository.save(user).getId();
    }

    @Transactional
    public void update(Long id, UserUpdateDto updateDto) {
        User user = getUser(id);
        user.addYear(updateDto.getEntranceYear(), updateDto.getGraduationYear());
    }

    @Transactional
    public void updateRole(Long id, String role) {
        User user = getUser(id);
        if (role.equals("ADMIN")) {
            user.updateRole(Role.ADMIN);
        } else if (role.equals("USER")) {
            user.updateRole(Role.USER);
        }
    }

    @Transactional
    public void delete(Long id) {
        User findUser = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 user 가 존재하지 않습니다. id=" + id));
        // 영속성 컨텍스트에 등록
        Blog blogByUserId = blogRepository.findBlogByUserId(id);
        List<Comment> comments = commentRepository.findByWriterUserId(id);
        List<Question> questions = questionRepository.findQuestionByUserId(id);
        List<Question> targetQuestions = questionRepository.findQuestionByTargetUserId(id);
        List<Posts> posts;

        if (!comments.isEmpty()) {
            for (Comment comment : comments) {
                if (comment != null) {
                    commentRepository.delete(comment);
                }
            }
        }

        deleteQuestions(questions);
        deleteQuestions(targetQuestions);

        if (blogByUserId != null) {
            posts = postsRepository.findPostsByBlogId(blogByUserId.getId());
            for (Posts post : posts) {
                postsRepository.deleteById(post.getId());
            }
            blogRepository.delete(blogByUserId);
        }
        userRepository.delete(findUser);
    }

    private void deleteQuestions(List<Question> questions) {
        if (!questions.isEmpty()) {
            for (Question question : questions) {
                List<Comment> innerComment = commentRepository.findByQuestionId(question.getId());

                if (innerComment != null) {
                    for (Comment comment : innerComment) {
                        commentRepository.delete(comment);
                    }
                }
                questionRepository.delete(question);
            }
        }
    }

    public UserMainResponseDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 user 가 존재하지 않습니다. id=" + id));
        return new UserMainResponseDto(user);
    }

    /**
     * token 으로 회원 가입
     */
    public String createToken(String email) {
        //비밀번호 확인 등의 유효성 검사 진행
        return jwtTokenProvider.createToken(email);
    }

    public String signUp(UserSaveRequestDto requestDto) { // 회원가입
        save(requestDto.toEntity());
        return signIn(requestDto);
    }

    @Transactional
    public String signIn(UserSaveRequestDto requestDto) { // 로그인
        User findUser = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));
        requestDto.addYear(findUser.getEntranceYear(), findUser.getGraduationYear());
        // 로그인 전 변경 사항이 있는지 체크 findUser
        findUser.update(requestDto.toEntity());
        return createToken(findUser.getEmail());
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        // Bearer 검증에 null 값을 넘기기 위해 일부러 이렇게 작성함
        return user.orElse(null);
    }

    public List<UserMainResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserMainResponseDto(user))
                .collect(Collectors.toList());
    }

    public String adminSignIn(LoginRequest loginRequest) {
        User user = userRepository.findByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPwd()).orElseThrow(() ->
                new IllegalArgumentException("email과 password가 일치하지 않습니다. "));
        return createToken(user.getEmail());
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /* admin 전용 */
    public Page<UserMainResponseDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(users -> new UserMainResponseDto(users));
    }

    public Page<UserMainResponseDto> findByUserName(Pageable pageable, String username) {
        return userRepository.findByUserName(pageable, username)
                .map(user -> new UserMainResponseDto(user));
    }

    public Page<UserMainResponseDto> findByUserRole(Pageable pageable, String role) {
        Role findRole = Role.valueOf(role.toUpperCase(Locale.ROOT));
        try {
            return userRepository.findByRoleAdmin(pageable, findRole)
                    .map(users -> new UserMainResponseDto(users));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("일치하는 권한이 없습니다. ");
        }
    }

    public UserMainResponseDto findByIdByAdmin(Long id) {
        return new UserMainResponseDto(getUser(id));
    }
}