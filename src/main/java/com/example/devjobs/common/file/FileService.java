package com.example.devjobs.common.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private final Path fileStorageLocation;

    public FileService(@Value("${app.file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("파일을 업로드할 디렉토리를 생성할 수 없습니다.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // 파일 이름 정규화
        String originalFileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // 파일 이름에 유효하지 않은 문자가 있는지 확인
            if (originalFileName.contains("..")) {
                throw new RuntimeException("파일명에 부적절한 문자가 포함되어 있습니다: " + originalFileName);
            }

            // 고유한 파일 이름 생성
            String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

            // 파일을 저장할 경로 확인
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("파일을 저장할 수 없습니다. 파일명: " + originalFileName, ex);
        }
    }

    public String getFileUrl(String fileName) {
        // 이 URL은 정적 리소스 핸들러를 통해 외부에서 접근 가능해야 합니다.
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/") // 예: /uploads/파일이름.jpg
                .path(fileName)
                .toUriString();
    }
}
