package com.example.curea.User.Entity;

import com.example.curea.User.Entity.enurmurate.SocialProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)", unique = true, nullable = false, updatable = false)
    private UUID publicId;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 255)
    private String socialId;

    @Column()
    private Short age;

    @Enumerated(EnumType.STRING)
    @Column()
    private SocialProvider socialProvider;

    public static User signIn(
        final String email,
        final String name,
        final String socialId,
        final SocialProvider socialProvider
    ) {
        User user = new User();
        user.publicId = UUID.randomUUID();
        user.email = email;
        user.name = name;
        user.socialId = socialId;
        user.socialProvider = socialProvider;
        return user;
    }
}
