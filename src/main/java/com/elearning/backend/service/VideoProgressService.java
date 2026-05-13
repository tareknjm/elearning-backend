package com.elearning.backend.service;

import com.elearning.backend.dto.CourseProgressResponse;
import com.elearning.backend.model.*;
import com.elearning.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoProgressService {

    private final VideoProgressRepository videoProgressRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;

    // Marquer une vidéo comme vue
    public void markAsWatched(Long videoId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video introuvable"));

        // Vérifier si déjà marquée
        videoProgressRepository.findByUserAndVideo(user, video).ifPresentOrElse(
                progress -> {
                    progress.setWatched(true);
                    videoProgressRepository.save(progress);
                },
                () -> {
                    VideoProgress progress = VideoProgress.builder()
                            .user(user)
                            .video(video)
                            .course(video.getCourse())
                            .watched(true)
                            .build();
                    videoProgressRepository.save(progress);
                }
        );
    }

    // Obtenir la progression d'un cours
    public CourseProgressResponse getCourseProgress(Long courseId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Formation introuvable"));

        List<Video> allVideos = videoRepository.findByCourseOrderByOrderIndexAsc(course);
        int total = allVideos.size();

        long watched = videoProgressRepository.countByUserAndCourseAndWatched(user, course, true);

        int percent = total > 0 ? (int) ((watched * 100) / total) : 0;
        boolean completed = percent == 100 && total > 0;

        return new CourseProgressResponse(courseId, total, (int) watched, percent, completed);
    }

    // Vérifier si une vidéo est vue
    public boolean isVideoWatched(Long videoId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video introuvable"));

        return videoProgressRepository.findByUserAndVideo(user, video)
                .map(VideoProgress::isWatched)
                .orElse(false);
    }
}