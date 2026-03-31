package com.skillforge.backend.service;

import com.skillforge.backend.dto.AdminAnalyticsResponse;
import com.skillforge.backend.dto.AdminQuestionCreateRequest;
import com.skillforge.backend.dto.AdminQuestionGenerateRequest;
import com.skillforge.backend.dto.AdminQuestionSummaryResponse;
import com.skillforge.backend.dto.AdminUserSummaryResponse;
import com.skillforge.backend.dto.AuthResponse;
import com.skillforge.backend.dto.ExamGenerationRequest;
import com.skillforge.backend.dto.ExamGenerationResponse;
import com.skillforge.backend.dto.ExamQuestionResponse;
import com.skillforge.backend.dto.ExamAttemptResponse;
import com.skillforge.backend.dto.LoginRequest;
import com.skillforge.backend.dto.CourseLessonProgressResponse;
import com.skillforge.backend.dto.CourseCertificateRequest;
import com.skillforge.backend.dto.CourseCertificateResponse;
import com.skillforge.backend.dto.LessonProgressItemResponse;
import com.skillforge.backend.dto.RegisterRequest;
import com.skillforge.backend.dto.SaveExamAttemptRequest;
import com.skillforge.backend.dto.SaveLessonProgressRequest;
import com.skillforge.backend.dto.StudentProgressResponse;
import com.skillforge.backend.dto.UserProfileResponse;
import com.skillforge.backend.entity.AppUser;
import com.skillforge.backend.entity.ExamAttempt;
import com.skillforge.backend.entity.CourseCertificate;
import com.skillforge.backend.entity.LessonCompletion;
import com.skillforge.backend.entity.QuestionBankItem;
import com.skillforge.backend.entity.Role;
import com.skillforge.backend.repository.ExamAttemptRepository;
import com.skillforge.backend.repository.CourseCertificateRepository;
import com.skillforge.backend.repository.LessonCompletionRepository;
import com.skillforge.backend.repository.QuestionBankRepository;
import com.skillforge.backend.repository.UserRepository;
import com.skillforge.backend.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final CourseCertificateRepository courseCertificateRepository;
    private final LessonCompletionRepository lessonCompletionRepository;
    private final QuestionBankRepository questionBankRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       ExamAttemptRepository examAttemptRepository,
                       CourseCertificateRepository courseCertificateRepository,
                       LessonCompletionRepository lessonCompletionRepository,
                       QuestionBankRepository questionBankRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.courseCertificateRepository = courseCertificateRepository;
        this.lessonCompletionRepository = lessonCompletionRepository;
        this.questionBankRepository = questionBankRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        Role role = parseRole(request.getRole());
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailAndRole(normalizedEmail, role)) {
            throw new IllegalArgumentException("Account already exists for this role and email.");
        }

        AppUser user = new AppUser();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        AppUser savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                token,
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                "Account created successfully."
        );
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        Role requestedRole = parseRole(request.getRole());

        AppUser user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        if (user.getRole() != requestedRole || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(
                token,
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                "Login successful."
        );
    }

    public UserProfileResponse getProfile(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        return new UserProfileResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }

    public List<AdminUserSummaryResponse> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(user -> new AdminUserSummaryResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    public AdminUserSummaryResponse createAdminUser(RegisterRequest request) {
        Role role = parseRole(request.getRole());
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        AppUser user = new AppUser();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        AppUser savedUser = userRepository.save(user);
        return new AdminUserSummaryResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getCreatedAt()
        );
    }

    public AdminAnalyticsResponse getAdminAnalytics() {
        return new AdminAnalyticsResponse(
                userRepository.count(),
                userRepository.countByRole(Role.STUDENT),
                userRepository.countByRole(Role.TRAINER),
                userRepository.countByRole(Role.ADMIN),
                userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(30))
        );
    }

    public List<AdminQuestionSummaryResponse> getQuestionBank() {
        return questionBankRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toQuestionSummary)
                .toList();
    }

    public AdminQuestionSummaryResponse createQuestion(AdminQuestionCreateRequest request) {
        String topic = request.getTopic().trim();
        String difficulty = normalizeDifficulty(request.getDifficulty());

        if (questionBankRepository.countByTopicIgnoreCase(topic) >= 30) {
            throw new IllegalArgumentException("This subject already has the maximum 30 questions.");
        }

        QuestionBankItem item = new QuestionBankItem();
        item.setTopic(topic);
        item.setDifficulty(difficulty);
        item.setQuestionText(request.getQuestionText().trim());
        item.setUsageCount(request.getUsageCount() == null ? 0 : request.getUsageCount());

        return toQuestionSummary(questionBankRepository.save(item));
    }

    public AdminQuestionSummaryResponse updateQuestion(Long id, AdminQuestionCreateRequest request) {
        QuestionBankItem item = questionBankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found."));

        String topic = request.getTopic().trim();
        String difficulty = normalizeDifficulty(request.getDifficulty());

        if (questionBankRepository.countByTopicIgnoreCaseAndIdNot(topic, id) >= 30) {
            throw new IllegalArgumentException("This subject already has the maximum 30 questions.");
        }

        item.setTopic(topic);
        item.setDifficulty(difficulty);
        item.setQuestionText(request.getQuestionText().trim());
        item.setUsageCount(request.getUsageCount() == null ? 0 : request.getUsageCount());

        return toQuestionSummary(questionBankRepository.save(item));
    }

    public void deleteQuestion(Long id) {
        if (!questionBankRepository.existsById(id)) {
            throw new IllegalArgumentException("Question not found.");
        }
        questionBankRepository.deleteById(id);
    }

    public List<AdminQuestionSummaryResponse> generateQuestions(AdminQuestionGenerateRequest request) {
        String topic = request.getTopic().trim();
        String difficulty = normalizeDifficulty(request.getDifficulty());
        int requestedCount = request.getCount() == null ? 3 : request.getCount();

        long existingCount = questionBankRepository.countByTopicIgnoreCase(topic);
        if (existingCount >= 30) {
            throw new IllegalArgumentException("This subject already has the maximum 30 questions.");
        }

        int allowedCount = (int) Math.min(requestedCount, 30 - existingCount);
        List<String> generatedPrompts = buildGeneratedPrompts(topic, difficulty, allowedCount);
        List<QuestionBankItem> generatedItems = new ArrayList<>();

        for (int index = 0; index < generatedPrompts.size(); index++) {
            QuestionBankItem item = new QuestionBankItem();
            item.setTopic(topic);
            item.setDifficulty(difficulty);
            item.setQuestionText(generatedPrompts.get(index));
            item.setUsageCount(0);
            generatedItems.add(item);
        }

        return questionBankRepository.saveAll(generatedItems)
                .stream()
                .map(this::toQuestionSummary)
                .toList();
    }

    public ExamGenerationResponse generateExam(ExamGenerationRequest request) {
        String difficulty = normalizeDifficulty(request.getDifficulty());
        List<String> normalizedTopics = request.getTopics().stream()
                .map(String::trim)
                .filter(topic -> !topic.isBlank())
                .distinct()
                .collect(Collectors.toList());
        int requestedCount = request.getQuestionCount();

        List<QuestionBankItem> storedQuestions = questionBankRepository
                .findByTopicInAndDifficultyOrderByUsageCountDescCreatedAtDesc(normalizedTopics, difficulty)
                .stream()
                .limit(requestedCount)
                .toList();

        List<ExamQuestionResponse> questions = new ArrayList<>();
        for (int index = 0; index < storedQuestions.size(); index++) {
            QuestionBankItem item = storedQuestions.get(index);
            questions.add(toExamQuestionResponse(
                    item.getId(),
                    item.getTopic(),
                    item.getDifficulty(),
                    item.getQuestionText(),
                    index
            ));
        }

        int missingCount = requestedCount - questions.size();
        if (missingCount > 0) {
            questions.addAll(buildGeneratedExamQuestions(normalizedTopics, difficulty, missingCount, questions.size()));
        }

        return new ExamGenerationResponse(
                normalizedTopics,
                difficulty,
                requestedCount,
                questions.size(),
                questions
        );
    }

    public ExamAttemptResponse saveExamAttempt(String email, SaveExamAttemptRequest request) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        int questionCount = request.getQuestionCount();
        int correctAnswers = request.getCorrectAnswers();
        int scorePercentage = request.getScorePercentage() != null
                ? request.getScorePercentage()
                : (questionCount <= 0 ? 0 : Math.round((correctAnswers * 100f) / questionCount));

        ExamAttempt attempt = new ExamAttempt();
        attempt.setUser(user);
        attempt.setTopicSummary(String.join(", ", request.getTopics()));
        attempt.setAssessmentType(request.getAssessmentType().trim());
        attempt.setDifficulty(normalizeDifficulty(request.getDifficulty()));
        attempt.setQuestionCount(questionCount);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setAnsweredCount(request.getAnsweredCount());
        attempt.setTimeLimitMinutes(request.getTimeLimitMinutes());
        attempt.setScorePercentage(scorePercentage);

        return toExamAttemptResponse(examAttemptRepository.save(attempt));
    }

    public StudentProgressResponse getStudentProgress(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        List<ExamAttemptResponse> attempts = examAttemptRepository.findByUserIdOrderBySubmittedAtDesc(user.getId())
                .stream()
                .map(this::toExamAttemptResponse)
                .toList();

        int examsCompleted = attempts.size();
        int averageScore = examsCompleted == 0
                ? 0
                : Math.round((float) attempts.stream().mapToInt(ExamAttemptResponse::getScorePercentage).sum() / examsCompleted);
        int bestScore = attempts.stream().mapToInt(ExamAttemptResponse::getScorePercentage).max().orElse(0);
        int latestScore = attempts.isEmpty() ? 0 : attempts.get(0).getScorePercentage();

        return new StudentProgressResponse(
                examsCompleted,
                averageScore,
                bestScore,
                latestScore,
                attempts.stream().limit(10).toList()
        );
    }

    @Transactional
    public CourseLessonProgressResponse saveLessonProgress(String email, SaveLessonProgressRequest request) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        String topic = request.getTopic().trim();
        String lessonKey = request.getLessonKey().trim();

        if (request.isCompleted()) {
            LessonCompletion completion = lessonCompletionRepository
                    .findByUserIdAndTopicAndLessonKey(user.getId(), topic, lessonKey)
                    .orElseGet(LessonCompletion::new);

            completion.setUser(user);
            completion.setTopic(topic);
            completion.setLessonKey(lessonKey);
            completion.setLessonTitle(request.getLessonTitle().trim());
            lessonCompletionRepository.save(completion);
        } else {
            lessonCompletionRepository.deleteByUserIdAndTopicAndLessonKey(user.getId(), topic, lessonKey);
        }

        return getCourseLessonProgress(email, topic, null);
    }

    public CourseLessonProgressResponse getCourseLessonProgress(String email, String topic, Integer totalLessons) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        String normalizedTopic = topic.trim();
        List<LessonProgressItemResponse> completedLessons = lessonCompletionRepository
                .findByUserIdAndTopicOrderByCompletedAtAsc(user.getId(), normalizedTopic)
                .stream()
                .map(item -> new LessonProgressItemResponse(
                        item.getLessonKey(),
                        item.getLessonTitle(),
                        item.getCompletedAt()
                ))
                .toList();

        int completedCount = completedLessons.size();
        int lessonCount = totalLessons != null ? totalLessons : completedCount;

        return new CourseLessonProgressResponse(
                normalizedTopic,
                completedCount,
                lessonCount,
                completedLessons
        );
    }

    public CourseCertificateResponse getCourseCertificate(String email, String topic) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        CourseCertificate certificate = courseCertificateRepository.findByUserIdAndTopic(user.getId(), topic.trim())
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found for this course."));

        return toCourseCertificateResponse(certificate);
    }

    public CourseCertificateResponse getCourseCertificateByCode(String certificateCode) {
        CourseCertificate certificate = courseCertificateRepository.findByCertificateCode(certificateCode.trim())
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found."));

        return toCourseCertificateResponse(certificate);
    }

    @Transactional
    public CourseCertificateResponse generateCourseCertificate(String email, CourseCertificateRequest request) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        String topic = request.getTopic().trim();
        int totalLessons = request.getTotalLessons();
        long completedCount = lessonCompletionRepository.countByUserIdAndTopic(user.getId(), topic);

        if (completedCount < totalLessons) {
            throw new IllegalArgumentException("Complete all lessons in this course before generating the certificate.");
        }

        CourseCertificate certificate = courseCertificateRepository.findByUserIdAndTopic(user.getId(), topic)
                .orElseGet(CourseCertificate::new);

        certificate.setUser(user);
        certificate.setTopic(topic);
        certificate.setCompletedLessons((int) completedCount);
        certificate.setTotalLessons(totalLessons);
        if (certificate.getCertificateCode() == null || certificate.getCertificateCode().isBlank()) {
            certificate.setCertificateCode(buildCertificateCode(user.getId(), topic));
        }

        return toCourseCertificateResponse(courseCertificateRepository.save(certificate));
    }

    private AdminQuestionSummaryResponse toQuestionSummary(QuestionBankItem item) {
        return new AdminQuestionSummaryResponse(
                item.getId(),
                item.getTopic(),
                item.getDifficulty(),
                item.getQuestionText(),
                item.getUsageCount(),
                item.getCreatedAt()
        );
    }

    private ExamAttemptResponse toExamAttemptResponse(ExamAttempt attempt) {
        return new ExamAttemptResponse(
                attempt.getId(),
                attempt.getTopicSummary(),
                attempt.getAssessmentType(),
                attempt.getDifficulty(),
                attempt.getQuestionCount(),
                attempt.getCorrectAnswers(),
                attempt.getScorePercentage(),
                attempt.getAnsweredCount(),
                attempt.getTimeLimitMinutes(),
                attempt.getSubmittedAt()
        );
    }

    private CourseCertificateResponse toCourseCertificateResponse(CourseCertificate certificate) {
        return new CourseCertificateResponse(
                certificate.getId(),
                certificate.getUser().getFullName(),
                certificate.getUser().getEmail(),
                certificate.getTopic(),
                certificate.getCertificateCode(),
                certificate.getCompletedLessons(),
                certificate.getTotalLessons(),
                certificate.getIssuedAt()
        );
    }

    private String buildCertificateCode(Long userId, String topic) {
        String normalized = topic.toUpperCase().replaceAll("[^A-Z0-9]+", "").trim();
        if (normalized.length() > 8) {
            normalized = normalized.substring(0, 8);
        }
        return "SF-" + normalized + "-" + userId + "-" + System.currentTimeMillis();
    }

    private String normalizeDifficulty(String difficulty) {
        String normalized = difficulty.trim().toLowerCase();
        return switch (normalized) {
            case "beginner" -> "Beginner";
            case "intermediate" -> "Intermediate";
            case "advanced" -> "Advanced";
            default -> throw new IllegalArgumentException("Difficulty must be Beginner, Intermediate, or Advanced.");
        };
    }

    private List<String> buildGeneratedPrompts(String topic, String difficulty, int count) {
        Map<String, List<String>> promptLibrary = new LinkedHashMap<>();
        promptLibrary.put("beginner", List.of(
                "What is the main purpose of %s in %s?",
                "Which basic concept should a learner understand first in %s when studying %s?",
                "How would you explain a beginner-friendly example of %s in %s?",
                "What common mistake do beginners make with %s in %s?",
                "Why is %s important when starting to learn %s?"
        ));
        promptLibrary.put("intermediate", List.of(
                "How would you apply %s to solve a practical %s problem?",
                "What trade-offs should be considered when using %s in %s?",
                "How does %s interact with other core concepts in %s?",
                "What debugging strategy would you use for %s issues in %s?",
                "How can performance or maintainability be improved when working with %s in %s?"
        ));
        promptLibrary.put("advanced", List.of(
                "How would you evaluate architectural trade-offs around %s in %s?",
                "What edge cases should be considered when scaling %s in %s systems?",
                "How would you design an interview-level solution involving %s in %s?",
                "What advanced optimization techniques apply to %s in %s?",
                "How would you compare multiple approaches for handling %s in %s?"
        ));

        List<String> templates = promptLibrary.get(difficulty.toLowerCase());
        List<String> prompts = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            String focusArea = focusAreaFor(topic, difficulty, index);
            String template = templates.get(index % templates.size());
            prompts.add(String.format(template, focusArea, topic));
        }
        return prompts;
    }

    private List<ExamQuestionResponse> buildGeneratedExamQuestions(List<String> topics,
                                                                   String difficulty,
                                                                   int count,
                                                                   int startIndex) {
        List<ExamQuestionResponse> generatedQuestions = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            String topic = topics.get(index % topics.size());
            String questionText = buildGeneratedPrompts(topic, difficulty, startIndex + index + 1)
                    .get(startIndex + index);

            generatedQuestions.add(toExamQuestionResponse(
                    -1L * (startIndex + index + 1),
                    topic,
                    difficulty,
                    questionText,
                    startIndex + index
            ));
        }
        return generatedQuestions;
    }

    private ExamQuestionResponse toExamQuestionResponse(Long id,
                                                        String topic,
                                                        String difficulty,
                                                        String questionText,
                                                        int seed) {
        int correctOptionIndex = Math.floorMod(seed, 4);
        List<String> options = buildMcqOptions(topic, difficulty, questionText, correctOptionIndex);

        return new ExamQuestionResponse(
                id,
                topic,
                difficulty,
                questionText,
                options,
                correctOptionIndex
        );
    }

    private List<String> buildMcqOptions(String topic,
                                         String difficulty,
                                         String questionText,
                                         int correctOptionIndex) {
        String focusArea = extractFocusArea(questionText, topic, difficulty);
        String correctOption = "A practical explanation that correctly applies " + focusArea + " in " + topic + ".";
        List<String> distractors = List.of(
                "A choice that treats " + focusArea + " as unrelated to real " + topic + " workflows.",
                "A choice that removes structure and testing from " + topic + " when using " + focusArea + ".",
                "A choice that assumes " + focusArea + " is only useful for styling and not for " + topic + " logic."
        );

        List<String> options = new ArrayList<>(List.of("", "", "", ""));
        options.set(correctOptionIndex, correctOption);

        int distractorCursor = 0;
        for (int index = 0; index < options.size(); index++) {
            if (index == correctOptionIndex) {
                continue;
            }
            options.set(index, distractors.get(distractorCursor));
            distractorCursor += 1;
        }

        return options;
    }

    private String extractFocusArea(String questionText, String topic, String difficulty) {
        String normalizedQuestion = questionText == null ? "" : questionText.toLowerCase();
        List<String> knownAreas = Map.ofEntries(
                Map.entry("java", List.of("oop", "collections", "exception handling", "multithreading", "streams")),
                Map.entry("dsa", List.of("arrays", "linked lists", "trees", "graphs", "dynamic programming")),
                Map.entry("oops", List.of("encapsulation", "inheritance", "polymorphism", "abstraction", "interfaces")),
                Map.entry("core java", List.of("collections", "exception handling", "strings", "multithreading", "file handling")),
                Map.entry("advanced java", List.of("jdbc", "servlets", "jsp", "spring boot", "multithreading")),
                Map.entry("javascript", List.of("scope", "asynchronous programming", "closures", "event handling", "modules")),
                Map.entry("react", List.of("state management", "component composition", "rendering", "hooks", "performance")),
                Map.entry("typescript", List.of("type safety", "generics", "interfaces", "type narrowing", "utility types")),
                Map.entry("node.js", List.of("event loop", "streams", "middleware", "api design", "error handling")),
                Map.entry("python", List.of("data structures", "functions", "decorators", "comprehensions", "error handling")),
                Map.entry("databases", List.of("indexes", "transactions", "normalization", "joins", "query optimization")),
                Map.entry("system design", List.of("scalability", "caching", "load balancing", "rate limiting", "fault tolerance"))
        ).getOrDefault(topic.trim().toLowerCase(), Collections.emptyList());

        for (String area : knownAreas) {
            if (normalizedQuestion.contains(area.toLowerCase())) {
                return area;
            }
        }

        return focusAreaFor(topic, difficulty, 0);
    }

    private String focusAreaFor(String topic, String difficulty, int index) {
        Map<String, List<String>> topicAreas = Map.ofEntries(
                Map.entry("java", List.of("oop", "collections", "exception handling", "multithreading", "streams")),
                Map.entry("dsa", List.of("arrays", "linked lists", "trees", "graphs", "dynamic programming")),
                Map.entry("oops", List.of("encapsulation", "inheritance", "polymorphism", "abstraction", "interfaces")),
                Map.entry("core java", List.of("collections", "exception handling", "strings", "multithreading", "file handling")),
                Map.entry("advanced java", List.of("jdbc", "servlets", "jsp", "spring boot", "multithreading")),
                Map.entry("javascript", List.of("scope", "asynchronous programming", "closures", "event handling", "modules")),
                Map.entry("react", List.of("state management", "component composition", "rendering", "hooks", "performance")),
                Map.entry("typescript", List.of("type safety", "generics", "interfaces", "type narrowing", "utility types")),
                Map.entry("node.js", List.of("event loop", "streams", "middleware", "API design", "error handling")),
                Map.entry("python", List.of("data structures", "functions", "decorators", "comprehensions", "error handling")),
                Map.entry("databases", List.of("indexes", "transactions", "normalization", "joins", "query optimization")),
                Map.entry("system design", List.of("scalability", "caching", "load balancing", "rate limiting", "fault tolerance"))
        );

        List<String> defaults = List.of("fundamentals", "practical implementation", "debugging", "optimization", "trade-offs");
        List<String> areas = topicAreas.getOrDefault(topic.trim().toLowerCase(), defaults);
        return areas.get(index % areas.size());
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role.trim().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }
}
