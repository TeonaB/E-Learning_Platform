package com.elearning.platform;

import com.elearning.platform.domain.*;
import com.elearning.platform.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class E2EIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setup() {
        reviewRepository.deleteAll();
        lessonRepository.deleteAll();
        courseRepository.deleteAll();
        categoryRepository.deleteAll();
        userProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testScenario1_UserRegistrationLoginAndProfileCreation() throws Exception {
        // 1. Register a new user via Web form submission
        mockMvc.perform(post("/web/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "e2e_student")
                        .param("email", "e2e_student@test.com")
                        .param("password", "studentPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/home"));

        User registeredUser = userRepository.findByEmail("e2e_student@test.com").orElse(null);
        assertNotNull(registeredUser);
        assertEquals("e2e_student", registeredUser.getUsername());

        // 2. Log in via Web form submission to establish session
        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/web/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "e2e_student@test.com")
                        .param("password", "studentPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/home"))
                .andReturn().getRequest().getSession();

        assertNotNull(session);
        User currentUserInSession = (User) session.getAttribute("currentUser");
        assertNotNull(currentUserInSession);
        assertEquals("e2e_student", currentUserInSession.getUsername());

        // 3. View profile form page (expects profile isNew to be true)
        mockMvc.perform(get("/web/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attribute("isNew", true));

        // 4. Create profile details via form submission
        mockMvc.perform(post("/web/profile/save").session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "E2E")
                        .param("lastName", "Student")
                        .param("phoneNumber", "0799999999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify profile creation in the database
        UserProfile profile = userProfileRepository.findByUser_Id(registeredUser.getId()).orElse(null);
        assertNotNull(profile);
        assertEquals("E2E", profile.getFirstName());
        assertEquals("Student", profile.getLastName());
        assertEquals("0799999999", profile.getPhoneNumber());
    }

    @Test
    public void testScenario2_AdminCategoryCourseAndLessonAddition() throws Exception {
        // Create admin user in DB and simulate login session
        User adminUser = new User();
        adminUser.setUsername("admin_e2e");
        adminUser.setEmail("admin_e2e@test.com");
        adminUser.setPassword("adminPass");
        adminUser.setRole(Role.ADMIN);
        adminUser = userRepository.save(adminUser);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", adminUser);

        // 1. Create a Category
        mockMvc.perform(post("/web/admin/categories/save").session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "E2E Cloud Computing")
                        .param("description", "All things Docker, Kubernetes, AWS."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/categories"));

        Category category = categoryRepository.findByName("E2E Cloud Computing").orElse(null);
        assertNotNull(category);

        // 2. Create a Course under that Category
        mockMvc.perform(post("/web/admin/courses/save").session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "AWS Fundamentals")
                        .param("description", "Learn Core Services")
                        .param("categoryId", String.valueOf(category.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/courses"));

        List<Course> courses = courseRepository.findByCategoryId(category.getId());
        assertEquals(1, courses.size());
        Course course = courses.get(0);
        assertEquals("AWS Fundamentals", course.getTitle());

        // 3. Add a Lesson to the Course
        mockMvc.perform(post("/web/admin/lessons/save").session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "1. Introducing EC2")
                        .param("contentUrl", "https://aws.amazon.com/ec2/intro")
                        .param("durationMinutes", "20")
                        .param("courseId", String.valueOf(course.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/courses/" + course.getId() + "/lessons"));

        List<Lesson> lessons = lessonRepository.findByCourseId(course.getId());
        assertEquals(1, lessons.size());
        assertEquals("1. Introducing EC2", lessons.get(0).getTitle());
    }

    @Test
    public void testScenario3_UserEnrollmentAndReviewsRatingBounds() throws Exception {
        // Setup initial Category and Course
        Category category = new Category();
        category.setName("E2E Web");
        category.setDescription("Frontend/Backend");
        category = categoryRepository.save(category);

        Course courseToSave = new Course();
        courseToSave.setTitle("NextJS Mastery");
        courseToSave.setDescription("Vercel Framework");
        courseToSave.setCategory(category);
        final Course course = courseRepository.save(courseToSave);

        // Setup student user and simulate login session
        User studentUser = new User();
        studentUser.setUsername("reviewer_student");
        studentUser.setEmail("rev@student.com");
        studentUser.setPassword("studPass");
        studentUser.setRole(Role.USER);
        studentUser = userRepository.save(studentUser);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", studentUser);

        // 1. Enroll user in course
        mockMvc.perform(post("/web/courses/" + course.getId() + "/enroll").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/home"));

        // Refresh user from database to check courses
        User freshUser = userRepository.findById(studentUser.getId()).orElse(null);
        assertNotNull(freshUser);
        assertTrue(freshUser.getCourses().stream().anyMatch(c -> c.getId().equals(course.getId())));

        // 2. Leave review with VALID rating (e.g. 5) -> redirects to lessons list
        mockMvc.perform(post("/web/reviews/save/" + course.getId()).session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("rating", "5")
                        .param("comment", "Incredible learning path!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/courses/" + course.getId() + "/lessons"));

        // 3. Leave review with INVALID rating too high (e.g. 6) -> stays on review/form page showing validation errors
        mockMvc.perform(post("/web/reviews/save/" + course.getId()).session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("rating", "6")
                        .param("comment", "Invalid rating test"))
                .andExpect(status().isOk())
                .andExpect(view().name("review/form"))
                .andExpect(model().attributeHasFieldErrors("review", "rating"));

        // 4. Leave review with INVALID rating too low (e.g. 0) -> stays on review/form page showing validation errors
        mockMvc.perform(post("/web/reviews/save/" + course.getId()).session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("rating", "0")
                        .param("comment", "Invalid rating test low"))
                .andExpect(status().isOk())
                .andExpect(view().name("review/form"))
                .andExpect(model().attributeHasFieldErrors("review", "rating"));
    }

    @Test
    public void testAdminSortingAndPagination() throws Exception {
        User adminUser = new User();
        adminUser.setUsername("admin_sort");
        adminUser.setEmail("admin_sort@test.com");
        adminUser.setPassword("adminPass");
        adminUser.setRole(Role.ADMIN);
        adminUser = userRepository.save(adminUser);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", adminUser);

        // Test categories sorting
        mockMvc.perform(get("/web/admin/categories")
                        .session(session)
                        .param("sortBy", "name")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("category/list"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("sortBy", "name"))
                .andExpect(model().attribute("sortDir", "desc"));

        mockMvc.perform(get("/web/admin/categories")
                        .session(session)
                        .param("sortBy", "coursesCount")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("category/list"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("sortBy", "coursesCount"))
                .andExpect(model().attribute("sortDir", "desc"));

        // Test users sorting
        mockMvc.perform(get("/web/admin/users")
                        .session(session)
                        .param("sortBy", "username")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("sortBy", "username"))
                .andExpect(model().attribute("sortDir", "desc"));

        mockMvc.perform(get("/web/admin/users")
                        .session(session)
                        .param("sortBy", "coursesCount")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("sortBy", "coursesCount"))
                .andExpect(model().attribute("sortDir", "desc"));
    }
}
