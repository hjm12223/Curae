package com.example.curea.auth.entity;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Accessor {

    private final UUID publicId;

    public static Accessor user(UUID publicId) {
        return new Accessor(publicId);
    }
}

