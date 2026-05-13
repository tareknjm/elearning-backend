package com.elearning.backend.controller;

import com.elearning.backend.dto.VideoResponse;
import com.elearning.backend.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    // Instructor — upload vidéo
    @PostMapping("/instructor/videos/upload")
    public ResponseEntity<VideoResponse> uploadVideo(
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam(defaultValue = "0") Integer orderIndex,
            @RequestParam MultipartFile file) throws IOException {

        return ResponseEntity.ok(
                videoService.uploadVideo(courseId, title, orderIndex, file));
    }

    // Public — liste vidéos d'une formation
    @GetMapping("/videos/course/{courseId}")
    public ResponseEntity<List<VideoResponse>> getVideosByCourse(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(videoService.getVideosByCourse(courseId));
    }

    // Instructor — supprimer vidéo
    @DeleteMapping("/instructor/videos/{videoId}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long videoId) {
        videoService.deleteVideo(videoId);
        return ResponseEntity.noContent().build();
    }
}