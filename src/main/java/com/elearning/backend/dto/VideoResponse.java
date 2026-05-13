package com.elearning.backend.dto;

import com.elearning.backend.model.Video;
import lombok.Data;

@Data
public class VideoResponse {

    private Long id;
    private String title;
    private String url;
    private Integer orderIndex;
    private Long courseId;
    private String courseTitle;

    public static VideoResponse fromEntity(Video video) {
        VideoResponse response = new VideoResponse();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setUrl(video.getUrl());
        response.setOrderIndex(video.getOrderIndex());
        response.setCourseId(video.getCourse().getId());
        response.setCourseTitle(video.getCourse().getTitle());
        return response;
    }
}