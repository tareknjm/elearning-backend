package com.elearning.backend.service;

import com.elearning.backend.dto.CourseRequest;
import com.elearning.backend.dto.CourseResponse;
import com.elearning.backend.model.*;
import com.elearning.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final NotificationService notificationService;

    private CourseResponse enrich(Course course) {
        CourseResponse response = CourseResponse.fromEntity(course);
        Double avg = ratingRepository.findAverageStarsByCourse(course);
        long total = ratingRepository.countByCourse(course);
        response.setAverageRating(avg != null
                ? Math.round(avg * 10.0) / 10.0 : 0.0);
        response.setTotalRatings(total);
        return response;
    }

    private List<CourseResponse> enrichAndSort(List<Course> courses) {
        return courses.stream()
                .map(this::enrich)
                .sorted((a, b) -> Double.compare(
                        b.getAverageRating(), a.getAverageRating()))
                .collect(Collectors.toList());
    }

    public CourseResponse createCourse(CourseRequest request,
                                       String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructeur introuvable"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElse(null);
        }

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .level(request.getLevel())
                .status(Course.Status.PENDING)
                .instructor(instructor)
                .category(category)
                .build();

        Course saved = courseRepository.save(course);

        // Notifier tous les admins
        userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.ADMIN)
                .forEach(admin -> notificationService.send(
                        admin,
                        "Nouvelle formation à valider",
                        instructor.getName() + " a soumis \""
                                + request.getTitle() + "\" — en attente de validation",
                        "NEW_COURSE"
                ));

        return enrich(saved);
    }

    public List<CourseResponse> getApprovedCourses() {
        return enrichAndSort(courseRepository.findByStatus(Course.Status.APPROVED));
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation introuvable"));
        return enrich(course);
    }

    public List<CourseResponse> searchCourses(String keyword) {
        return enrichAndSort(courseRepository
                .findByStatusAndTitleContainingIgnoreCase(
                        Course.Status.APPROVED, keyword));
    }

    public List<CourseResponse> getMyCourses(String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructeur introuvable"));
        return courseRepository.findByInstructor(instructor)
                .stream()
                .map(this::enrich)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::enrich)
                .collect(Collectors.toList());
    }

    public CourseResponse approveCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation introuvable"));
        course.setStatus(Course.Status.APPROVED);
        Course saved = courseRepository.save(course);

        // Notifier l'instructeur
        if (course.getInstructor() != null) {
            notificationService.send(
                    course.getInstructor(),
                    "Formation approuvée ! ✅",
                    "Votre formation \"" + course.getTitle()
                            + "\" a été approuvée et est maintenant visible.",
                    "COURSE_APPROVED"
            );
        }

        return enrich(saved);
    }

    public CourseResponse rejectCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation introuvable"));
        course.setStatus(Course.Status.REJECTED);
        Course saved = courseRepository.save(course);

        // Notifier l'instructeur
        if (course.getInstructor() != null) {
            notificationService.send(
                    course.getInstructor(),
                    "Formation refusée",
                    "Votre formation \"" + course.getTitle()
                            + "\" a été refusée par l'administrateur.",
                    "COURSE_REJECTED"
            );
        }

        return enrich(saved);
    }

    public List<CourseResponse> getCoursesByCategory(Long categoryId) {
        return enrichAndSort(courseRepository.findAll().stream()
                .filter(c -> c.getStatus() == Course.Status.APPROVED
                        && c.getCategory() != null
                        && c.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList()));
    }
}