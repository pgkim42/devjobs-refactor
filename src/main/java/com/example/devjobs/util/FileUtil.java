package com.example.devjobs.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 파일 업로드 및 삭제와 관련된 유틸리티 클래스입니다.
 */
@Component
public class FileUtil {

    @Value("${app.file.upload-dir}")
    private String uploadPath;

    /**
     * 파일을 지정된 카테고리 폴더에 업로드합니다.
     *
     * @param multipartFile 업로드할 파일
     * @param category      파일을 저장할 하위 폴더명 (e.g., "jobposting", "resume")
     * @return 저장된 파일의 전체 경로, 실패 시 null
     */
    public String fileUpload(MultipartFile multipartFile, String category) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        try {
            Path uploadDirectory = Paths.get(uploadPath, category);

            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);
            }

            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            Path targetLocation = uploadDirectory.resolve(fileName);

            Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation.toString();
        } catch (IOException e) {
            // 실제 프로덕션에서는 로깅 프레임워크(e.g., SLF4J)를 사용하는 것이 좋습니다.
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 지정된 경로의 파일을 삭제합니다.
     *
     * @param filePath 삭제할 파일의 경로 (uploadPath 기준)
     * @return 삭제 성공 시 true, 실패 시 false
     */
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadPath, filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}