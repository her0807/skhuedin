package com.skhuedin.skhuedin.dto.user;

import com.skhuedin.skhuedin.domain.Provider;
import com.skhuedin.skhuedin.domain.User;
import com.skhuedin.skhuedin.infra.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserSaveRequestDto {

    private String email;
    private String password;
    private String name;
    private Provider provider;
    private String userImageUrl;
    LocalDateTime entranceYear;
    LocalDateTime graduationYear;

    @Builder
    public UserSaveRequestDto(String email,
                              String password, String name,
                              Provider provider, String userImageUrl,
                              LocalDateTime entranceYear, LocalDateTime graduationYear) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.provider = provider;
        this.userImageUrl = userImageUrl;
        this.entranceYear = entranceYear;
        this.graduationYear = graduationYear;
    }

    public User toEntity() {
        User user = User.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .provider(this.provider)
                .userImageUrl(this.userImageUrl)
                .entranceYear(this.entranceYear)
                .graduationYear(this.graduationYear)
                .role(Role.USER)
                .build();
        return user;
    }

    public User toEntity(User targetUser, UserSaveRequestDto requestDto) {
        User user = User.builder()
                .email(targetUser.getEmail())
                .password(targetUser.getPassword())
                .name(targetUser.getName())
                .provider(targetUser.getProvider())
                .userImageUrl(targetUser.getUserImageUrl())
                .entranceYear(requestDto.getEntranceYear())
                .graduationYear(requestDto.getGraduationYear())
                .role(Role.USER)
                .build();
        return user;
    }

}
