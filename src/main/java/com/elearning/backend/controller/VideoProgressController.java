package com.elearning.backend.controller;

import com.elearning.backend.dto.CourseProgressResponse;
import com.elearning.backend.service.VideoProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learner")
@RequiredArgsConstructor
public class VideoProgressController {

    private final VideoProgressService videoProgressService;

    @PostMapping("/videos/{videoId}/watch")
    public ResponseEntity<Void> markAsWatched(
            @PathVariable Long videoId,
            Authentication authentication) {
        videoProgressService.markAsWatched(videoId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<CourseProgressResponse> getCourseProgress(
            @PathVariable Long courseId,
            Authentication authentication) {
        return ResponseEntity.ok(
                videoProgressService.getCourseProgress(courseId, authentication.getName()));
    }
}