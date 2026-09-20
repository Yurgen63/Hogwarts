package ru.hogwarts.school.Controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Avatar> uploadAvatar(
            @Parameter(description = "ID студента")
            @RequestParam("studentId") Long studentId,

            @Parameter(description = "Файл аватара")
            @RequestPart("file") MultipartFile file) throws IOException {

        Avatar avatar = avatarService.uploadAvatar(studentId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(avatar);
    }

    @GetMapping("/student/{studentId}/data")
    public ResponseEntity<byte[]> getAvatarData(@PathVariable Long studentId) {
        Avatar avatar = avatarService.getAvatarByStudentId(studentId);
        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }

        String mediaType = avatar.getMediaType() != null
                ? avatar.getMediaType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mediaType));
        headers.setContentLength(avatar.getData() != null ? avatar.getData().length : 0);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(avatar.getData());
    }

    @GetMapping
    public Page<Avatar> getAvatars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return avatarService.getAllAvatars(page, size);
    }
}
