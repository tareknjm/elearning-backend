package com.elearning.backend.controller;

import com.elearning.backend.dto.*;
import com.elearning.backend.model.InstructorApplication;
import com.elearning.backend.service.InstructorApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InstructorApplicationController {

    private final InstructorApplicationService applicationService;

    // Public — soumettre une candidature
    @PostMapping("/applications/instructor")
    public ResponseEntity<InstructorApplicationResponse> apply(
            @Valid @RequestBody InstructorApplicationRequest request) {
        return ResponseEntity.ok(applicationService.apply(request));
    }

    // Admin — toutes les candidatures
    @GetMapping("/admin/applications")
    public ResponseEntity<List<InstructorApplicationResponse>>
    getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    // Admin — par statut
    @GetMapping("/admin/applications/status/{status}")
    public ResponseEntity<List<InstructorApplicationResponse>>
    getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(applicationService.getByStatus(
                InstructorApplication.Status.valueOf(status.toUpperCase())));
    }

    // Admin — détail
    @GetMapping("/admin/applications/{id}")
    public ResponseEntity<InstructorApplicationResponse>
    getById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getById(id));
    }

    // Admin — traiter une candidature
    @PutMapping("/admin/applications/{id}/review")
    public ResponseEntity<InstructorApplicationResponse> review(
            @PathVariable Long id,
            @RequestBody ReviewApplicationRequest request) {
        return ResponseEntity.ok(applicationService.review(id, request));
    }

    // Stats pour dashboard
    @GetMapping("/admin/applications/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(Map.of(
                "pending", applicationService.countPending()));
    }
}