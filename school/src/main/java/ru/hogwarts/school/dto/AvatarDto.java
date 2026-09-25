package ru.hogwarts.school.dto;

public record AvatarDto(

        Long id,
        String filePath,
        long fileSize,
        String mediaType,
        Long studentId
) {
}
