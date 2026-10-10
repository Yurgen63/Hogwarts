package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);
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
        logger.info("Was invoked method for upload avatar for student id: {}", studentId);
        logger.debug("File details: name={}, size={}, type={}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());
        Student student = studentService.getStudent(studentId);

        if (!Files.exists(avatarsDir)) {
            logger.debug("Creating avatars directory: {}", avatarsDir);
            Files.createDirectories(avatarsDir);
        }

        String fileName = studentId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = avatarsDir.resolve(fileName);

        Files.write(filePath, file.getBytes());
        logger.debug("File saved to: {}", filePath);

        Avatar avatar = avatarRepository.findByStudentId(studentId);
        logger.debug("Creating new avatar for student id: {}", studentId);
        if (avatar == null) {
            avatar = new Avatar();
            avatar.setStudent(student);
        } else {
            logger.debug("Updating existing avatar for student id: {}", studentId);
        }

        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        logger.info("Avatar saved for student id: {}", studentId);
        return avatarRepository.save(avatar);
    }

    @Transactional(readOnly = true)
    public Avatar getAvatarByStudentId(Long studentId) {
        logger.info("Was invoked method for get avatar by student id: {}", studentId);
        Avatar avatar = avatarRepository.findByStudentId(studentId);
        if (avatar == null) {
            logger.warn("Avatar not found for student id: {}", studentId);
        } else {
            logger.debug("Avatar found for student id: {}, size: {}", studentId, avatar.getFileSize());
        }
        return avatar;
    }

    @Transactional(readOnly = true)
    public Page<AvatarDto> getAllAvatars(int page, int size) {
        logger.info("Was invoked method for get all avatars with pagination: page={}, size={}", page, size);
        if (page < 0 || size <= 0) {
            logger.warn("Invalid pagination parameters: page={}, size={}", page, size);
            throw new IllegalArgumentException("Page must be >= 0 and size must be > 0");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Avatar> avatars = avatarRepository.findAll(pageable);
        logger.debug("Found {} avatars out of {}", avatars.getNumberOfElements(), avatars.getTotalElements());
        return avatars.map(a -> new AvatarDto(
                        a.getId(),
                        a.getFilePath(),
                        a.getFileSize(),
                        a.getMediaType(),
                        a.getStudent() != null ? a.getStudent().getId() : null
                ));
    }
}