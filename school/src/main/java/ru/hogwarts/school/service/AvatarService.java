package ru.hogwarts.school.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarDto;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentService studentService;

    private final Path avatarsDir;

    public AvatarService(AvatarRepository avatarRepository,
                         StudentService studentService,
                         @Value("${path.to.avatars.folder:avatars}") String avatarsDirPath) {
        this.avatarRepository = avatarRepository;
        this.studentService = studentService;
        this.avatarsDir = Paths.get(avatarsDirPath);
    }

    @Transactional
    public Avatar uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = studentService.getStudent(studentId);

        if (!Files.exists(avatarsDir)) {
            Files.createDirectories(avatarsDir);
        }

        String fileName = studentId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = avatarsDir.resolve(fileName);

        Files.write(filePath, file.getBytes());

        Avatar avatar = avatarRepository.findByStudentId(studentId);
        if (avatar == null) {
            avatar = new Avatar();
            avatar.setStudent(student);
        }

        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        return avatarRepository.save(avatar);
    }

    @Transactional(readOnly = true)
    public Avatar getAvatarByStudentId(Long studentId) {
        return avatarRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public Page<AvatarDto> getAllAvatars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return avatarRepository.findAll(pageable)
                .map(a -> new AvatarDto(
                        a.getId(),
                        a.getFilePath(),
                        a.getFileSize(),
                        a.getMediaType(),
                        a.getStudent() != null ? a.getStudent().getId() : null
                ));
    }
}