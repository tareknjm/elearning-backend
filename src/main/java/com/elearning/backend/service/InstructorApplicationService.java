package com.elearning.backend.service;

import com.elearning.backend.dto.*;
import com.elearning.backend.model.*;
import com.elearning.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorApplicationService {

    private final InstructorApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    // Soumettre une candidature
    public InstructorApplicationResponse apply(
            InstructorApplicationRequest request) {

        // Vérifier si email déjà utilisé
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                    "Un compte existe déjà avec cet email");
        }

        if (applicationRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                    "Une candidature existe déjà avec cet email");
        }

        InstructorApplication application = InstructorApplication.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .speciality(request.getSpeciality())
                .experience(request.getExperience())
                .motivation(request.getMotivation())
                .linkedinUrl(request.getLinkedinUrl())
                .educationLevel(request.getEducationLevel())
                .build();

        InstructorApplication saved =
                applicationRepository.save(application);

        // Notifier tous les admins
        userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.ADMIN)
                .forEach(admin -> notificationService.send(
                        admin,
                        "Nouvelle candidature instructeur",
                        request.getFullName()
                                + " a soumis une candidature pour devenir instructeur.",
                        "NEW_APPLICATION"
                ));

        return InstructorApplicationResponse.fromEntity(saved);
    }

    // Liste toutes les candidatures (Admin)
    public List<InstructorApplicationResponse> getAllApplications() {
        return applicationRepository.findAllByOrderByAppliedAtDesc()
                .stream()
                .map(InstructorApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Liste par statut
    public List<InstructorApplicationResponse> getByStatus(
            InstructorApplication.Status status) {
        return applicationRepository
                .findByStatusOrderByAppliedAtDesc(status)
                .stream()
                .map(InstructorApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Détail d'une candidature
    public InstructorApplicationResponse getById(Long id) {
        return InstructorApplicationResponse.fromEntity(
                applicationRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Candidature introuvable")));
    }

    // Approuver ou rejeter
    @Transactional
    public InstructorApplicationResponse review(
            Long id, ReviewApplicationRequest request) {

        InstructorApplication application = applicationRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Candidature introuvable"));

        application.setAdminNote(request.getAdminNote());
        application.setReviewedAt(LocalDateTime.now());

        if ("APPROVE".equals(request.getDecision())) {
            application.setStatus(InstructorApplication.Status.APPROVED);

            // Créer le compte instructeur
            String password = request.getTemporaryPassword() != null
                    && !request.getTemporaryPassword().isBlank()
                    ? request.getTemporaryPassword()
                    : "Instructor@2026";

            User instructor = User.builder()
                    .name(application.getFullName())
                    .email(application.getEmail())
                    .password(passwordEncoder.encode(password))
                    .role(User.Role.INSTRUCTOR)
                    .build();

            userRepository.save(instructor);

        } else {
            application.setStatus(InstructorApplication.Status.REJECTED);
        }

        return InstructorApplicationResponse.fromEntity(
                applicationRepository.save(application));
    }

    // Stats
    public long countPending() {
        return applicationRepository
                .findByStatusOrderByAppliedAtDesc(
                        InstructorApplication.Status.PENDING)
                .size();
    }
}