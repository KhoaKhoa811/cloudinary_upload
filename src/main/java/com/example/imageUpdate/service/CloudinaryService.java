package com.example.imageUpdate.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    @Autowired
    Cloudinary cloudinary;

    public Map uploadResource(MultipartFile multipartFile, String folderName, String resourceType) throws IOException {
        File file = convert(multipartFile);
        Map result = cloudinary.uploader().upload(file, 
                ObjectUtils.asMap(
                    "resource_type", resourceType,
                    "folder", folderName
                ));
        deleteTempFile(file);
        return result;
    }

    public Map<String, Object> uploadImage(MultipartFile multipartFile, String folderName) throws IOException {
        return uploadResource(multipartFile, folderName, "image");
    }

    public Map<String, Object> uploadVideo(MultipartFile multipartFile, String folderName) throws IOException {
        return uploadResource(multipartFile, folderName, "video");
    }

    public Map deleteResource(String id, String folderName, String resourceType) throws IOException {
        return cloudinary.uploader().destroy(id, 
                ObjectUtils.asMap(
                    "resource_type", resourceType,
                    "folder", folderName
                ));
    }

    public Map<String, Object> deleteImage(String id, String folderName) throws IOException {
        return deleteResource(id, folderName, "image");
    }

    public Map<String, Object> deleteVideo(String id, String folderName) throws IOException {
        return deleteResource(id, folderName, "video");
    }

    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
            fos.flush();
        }
        return file;
    }

    private void deleteTempFile(File file) throws IOException {
        if (!Files.deleteIfExists(file.toPath())) {
            throw new IOException("Failed to delete temporary file: " + file.getAbsolutePath());
        }
    }
}
