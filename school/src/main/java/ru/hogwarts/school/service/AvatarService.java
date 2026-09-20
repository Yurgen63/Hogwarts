package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
        this.avatarsDir = Paths.get(avatarsDirPath).toAbsolutePath();
    }

    public Avatar uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = studentService.getStudent(studentId);

        System.out.println(">>> Папка для аватаров: " + avatarsDir);
        Files.createDirectories(avatarsDir);

        byte[] data = file.getBytes();

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "avatar";
        }
        String fileName = studentId + "_" + System.currentTimeMillis() + "_" + originalName;
        Path filePath = avatarsDir.resolve(fileName);

        System.out.println(">>> Запись файла: " + filePath);
        Files.write(filePath, data);
        System.out.println(">>> Файл записан");

        Avatar avatar = avatarRepository.findByStudentId(studentId);
        if (avatar == null) {
            avatar = new Avatar();
            avatar.setStudent(student);
        }

        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(data.length);
        avatar.setMediaType(file.getContentType() != null
                ? file.getContentType()
                : "application/octet-stream");
        avatar.setData(data);

        System.out.println(">>> Сохранение в БД...");
        Avatar saved = avatarRepository.save(avatar);
        System.out.println(">>> Сохранено, id=" + saved.getId());
        return saved;
    }

    public Avatar getAvatarByStudentId(Long studentId) {
        return avatarRepository.findByStudentId(studentId);
    }

    public Page<Avatar> getAllAvatars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return avatarRepository.findAll(pageable);
    }
}