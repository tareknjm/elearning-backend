package com.elearning.backend.service;

import com.elearning.backend.model.*;
import com.elearning.backend.repository.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final QuizService quizService;

    public byte[] generateCertificate(Long courseId, String userEmail) throws Exception {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Formation introuvable"));

        // Vérifier que le learner a réussi le quiz
        if (!quizService.hasPassed(courseId, userEmail)) {
            throw new RuntimeException("Vous devez réussir le quiz pour obtenir le certificat");
        }

        return buildPdf(user, course);
    }

    private byte[] buildPdf(User user, Course course) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        // Couleurs
        BaseColor primaryColor = new BaseColor(99, 102, 241);
        BaseColor accentColor = new BaseColor(139, 92, 246);
        BaseColor goldColor = new BaseColor(245, 158, 11);
        BaseColor lightGray = new BaseColor(248, 250, 252);

        PdfContentByte cb = writer.getDirectContent();

        // Fond dégradé simulé
        cb.setColorFill(lightGray);
        cb.rectangle(0, 0, document.getPageSize().getWidth(),
                document.getPageSize().getHeight());
        cb.fill();

        // Bordure décorative extérieure
        cb.setColorStroke(primaryColor);
        cb.setLineWidth(8f);
        cb.rectangle(20, 20,
                document.getPageSize().getWidth() - 40,
                document.getPageSize().getHeight() - 40);
        cb.stroke();

        // Bordure intérieure dorée
        cb.setColorStroke(goldColor);
        cb.setLineWidth(2f);
        cb.rectangle(30, 30,
                document.getPageSize().getWidth() - 60,
                document.getPageSize().getHeight() - 60);
        cb.stroke();

        // Titre principal
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 42, Font.BOLD,
                primaryColor);
        Paragraph title = new Paragraph("CERTIFICAT DE RÉUSSITE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(50f);
        document.add(title);

        // Ligne dorée sous le titre
        cb.setColorStroke(goldColor);
        cb.setLineWidth(3f);
        float centerX = document.getPageSize().getWidth() / 2;
        cb.moveTo(centerX - 150, document.getPageSize().getHeight() - 130);
        cb.lineTo(centerX + 150, document.getPageSize().getHeight() - 130);
        cb.stroke();

        // Sous-titre
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC,
                new BaseColor(100, 100, 120));
        Paragraph subtitle = new Paragraph("E-Learning Platform", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingBefore(10f);
        document.add(subtitle);

        // Texte "décerné à"
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL,
                new BaseColor(80, 80, 100));
        Paragraph decerne = new Paragraph("Ce certificat est décerné à", normalFont);
        decerne.setAlignment(Element.ALIGN_CENTER);
        decerne.setSpacingBefore(40f);
        document.add(decerne);

        // Nom du learner
        Font nameFont = new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD,
                accentColor);
        Paragraph name = new Paragraph(user.getName(), nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        name.setSpacingBefore(10f);
        document.add(name);

        // Ligne sous le nom
        cb.setColorStroke(accentColor);
        cb.setLineWidth(1.5f);
        cb.moveTo(centerX - 200, document.getPageSize().getHeight() - 270);
        cb.lineTo(centerX + 200, document.getPageSize().getHeight() - 270);
        cb.stroke();

        // Texte formation
        Paragraph pour = new Paragraph("pour avoir complété avec succès la formation", normalFont);
        pour.setAlignment(Element.ALIGN_CENTER);
        pour.setSpacingBefore(25f);
        document.add(pour);

        // Titre du cours
        Font courseFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD,
                primaryColor);
        Paragraph courseTitle = new Paragraph(course.getTitle(), courseFont);
        courseTitle.setAlignment(Element.ALIGN_CENTER);
        courseTitle.setSpacingBefore(10f);
        document.add(courseTitle);

        // Instructeur
        Font instrFont = new Font(Font.FontFamily.HELVETICA, 13, Font.ITALIC,
                new BaseColor(120, 120, 140));
        Paragraph instructor = new Paragraph(
                "Dispensée par " + course.getInstructor().getName(), instrFont);
        instructor.setAlignment(Element.ALIGN_CENTER);
        instructor.setSpacingBefore(8f);
        document.add(instructor);

        // Date
        String date = java.time.LocalDate.now().toString();
        Paragraph datePara = new Paragraph("Délivré le " + date, instrFont);
        datePara.setAlignment(Element.ALIGN_CENTER);
        datePara.setSpacingBefore(40f);
        document.add(datePara);

        // Étoiles décoratives
        Font starFont = new Font(Font.FontFamily.HELVETICA, 20, Font.NORMAL, goldColor);
        Paragraph stars = new Paragraph("★  ★  ★", starFont);
        stars.setAlignment(Element.ALIGN_CENTER);
        stars.setSpacingBefore(15f);
        document.add(stars);

        document.close();
        return out.toByteArray();
    }
}