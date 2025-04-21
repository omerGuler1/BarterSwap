package com.barterswap.service;

import com.barterswap.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<String> uploadFiles(List<MultipartFile> files) {
        List<String> fileUrls = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                String fileName = generateUniqueFileName(file.getOriginalFilename());
                Path filePath = Paths.get(uploadDir, fileName);
                
                // Create directory if it doesn't exist
                Files.createDirectories(Paths.get(uploadDir));
                
                // Save the file
                Files.copy(file.getInputStream(), filePath);
                
                // Add the file URL to the list
                String fileUrl = "/uploads/" + fileName;
                fileUrls.add(fileUrl);
                
                log.info("File uploaded successfully: {}", fileUrl);
            } catch (IOException e) {
                log.error("Error uploading file: {}", e.getMessage());
                throw new FileUploadException("Failed to upload file: " + e.getMessage());
            }
        }
        
        return fileUrls;
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }
} 