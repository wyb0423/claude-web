package com.claude.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file,
                                                            @RequestParam(value = "sessionId", required = false) String sessionId) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (file.isEmpty()) {
                result.put("code", 400);
                result.put("message", "文件不能为空");
                return ResponseEntity.ok(result);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String savedFilename = UUID.randomUUID().toString() + extension;

            Path sessionDir = Paths.get(uploadDir);
            if (sessionId != null && !sessionId.isEmpty()) {
                sessionDir = sessionDir.resolve(sessionId);
            }
            Files.createDirectories(sessionDir);

            Path filePath = sessionDir.resolve(savedFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/api/file/download/" + (sessionId != null ? sessionId + "/" : "") + savedFilename;

            Map<String, Object> data = new HashMap<>();
            data.put("filename", savedFilename);
            data.put("originalFilename", originalFilename);
            data.put("size", file.getSize());
            data.put("url", fileUrl);

            result.put("code", 200);
            result.put("data", data);
            result.put("message", "上传成功");

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/upload/multiple")
    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
                                                                      @RequestParam(value = "sessionId", required = false) String sessionId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> uploadedFiles = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String originalFilename = file.getOriginalFilename();
                    String extension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String savedFilename = UUID.randomUUID().toString() + extension;

                    Path sessionDir = Paths.get(uploadDir);
                    if (sessionId != null && !sessionId.isEmpty()) {
                        sessionDir = sessionDir.resolve(sessionId);
                    }
                    Files.createDirectories(sessionDir);

                    Path filePath = sessionDir.resolve(savedFilename);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    String fileUrl = "/api/file/download/" + (sessionId != null ? sessionId + "/" : "") + savedFilename;

                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("filename", savedFilename);
                    fileInfo.put("originalFilename", originalFilename);
                    fileInfo.put("size", file.getSize());
                    fileInfo.put("url", fileUrl);
                    uploadedFiles.add(fileInfo);
                }
            }

            result.put("code", 200);
            result.put("data", uploadedFiles);
            result.put("message", "上传成功，共 " + uploadedFiles.size() + " 个文件");

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/download/**")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) {
        String path = request.getRequestURI();
        String filePath = path.replace("/api/file/download/", "");

        try {
            Path file = Paths.get(uploadDir).resolve(filePath);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                String originalFilename = file.getFileName().toString();
                String extension = "";
                if (originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
                }

                String contentType = "application/octet-stream";
                switch (extension) {
                    case "pdf":
                        contentType = "application/pdf";
                        break;
                    case "png":
                        contentType = "image/png";
                        break;
                    case "jpg":
                    case "jpeg":
                        contentType = "image/jpeg";
                        break;
                    case "gif":
                        contentType = "image/gif";
                        break;
                    case "txt":
                        contentType = "text/plain";
                        break;
                    case "html":
                        contentType = "text/html";
                        break;
                    case "css":
                        contentType = "text/css";
                        break;
                    case "js":
                        contentType = "application/javascript";
                        break;
                    case "json":
                        contentType = "application/json";
                        break;
                    case "xml":
                        contentType = "application/xml";
                        break;
                    case "zip":
                        contentType = "application/zip";
                        break;
                    case "doc":
                        contentType = "application/msword";
                        break;
                    case "docx":
                        contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                        break;
                    case "xls":
                        contentType = "application/vnd.ms-excel";
                        break;
                    case "xlsx":
                        contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                        break;
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listFiles(@RequestParam(value = "sessionId", required = false) String sessionId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Path dir = Paths.get(uploadDir);
            if (sessionId != null && !sessionId.isEmpty()) {
                dir = dir.resolve(sessionId);
            }

            if (!Files.exists(dir)) {
                result.put("code", 200);
                result.put("data", new ArrayList<>());
                result.put("message", "目录为空");
                return ResponseEntity.ok(result);
            }

            List<Map<String, Object>> files = new ArrayList<>();
            Files.list(dir).forEach(path -> {
                try {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("filename", path.getFileName().toString());
                    fileInfo.put("size", Files.size(path));
                    fileInfo.put("isDirectory", Files.isDirectory(path));
                    fileInfo.put("lastModified", Files.getLastModifiedTime(path).toMillis());
                    files.add(fileInfo);
                } catch (IOException e) {
                    // skip
                }
            });

            result.put("code", 200);
            result.put("data", files);
            result.put("message", "success");
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            result.put("code", 500);
            result.put("message", "获取文件列表失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteFile(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        String filename = (String) body.get("filename");
        String sessionId = (String) body.get("sessionId");

        if (filename == null || filename.isEmpty()) {
            result.put("code", 400);
            result.put("message", "文件名不能为空");
            return ResponseEntity.ok(result);
        }

        try {
            Path file = Paths.get(uploadDir);
            if (sessionId != null && !sessionId.isEmpty()) {
                file = file.resolve(sessionId);
            }
            file = file.resolve(filename);

            if (Files.exists(file)) {
                Files.delete(file);
                result.put("code", 200);
                result.put("message", "删除成功");
            } else {
                result.put("code", 404);
                result.put("message", "文件不存在");
            }
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}