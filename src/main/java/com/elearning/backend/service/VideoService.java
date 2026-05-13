package com.elearning.backend.service;

import com.elearning.backend.dto.VideoResponse;
import com.elearning.backend.model.Course;
import com.elearning.backend.model.Video;
import com.elearning.backend.repository.CourseRepository;
import com.elearning.backend.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public VideoResponse uploadVideo(Long courseId, String title,
                                     Integer orderIndex, MultipartFile file) throws IOException {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Formation introuvable"));

        // Créer le dossier si inexistant
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom unique pour le fichier
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // URL accessible depuis le frontend
        String fileUrl = "/videos/" + fileName;

        Video video = Video.builder()
                .title(title)
                .url(fileUrl)
                .orderIndex(orderIndex != null ? orderIndex : 0)
                .course(course)
                .build();

        return VideoResponse.fromEntity(videoRepository.save(video));
    }

    public List<VideoResponse> getVideosByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Formation introuvable"));
        return videoRepository.findByCourseOrderByOrderIndexAsc(course)
                .stream()
                .map(VideoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteVideo(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo introuvable"));

        // Supprimer le fichier physique
        try {
            String fileName = video.getUrl().replace("/videos/", "");
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Erreur suppression fichier: " + e.getMessage());
        }

        videoRepository.delete(video);
    }
}