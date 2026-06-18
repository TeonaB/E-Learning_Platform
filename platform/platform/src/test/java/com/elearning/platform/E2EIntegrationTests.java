package com.elearning.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class E2EIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testScenario1_UserRegistrationLoginAndProfileCreation() throws Exception {
        // 1. Register a new user
        Map<String, String> userDto = new HashMap<>();
        userDto.put("username", "e2e_student");
        userDto.put("email", "e2e_student@test.com");
        userDto.put("password", "studentPass123");
        userDto.put("role", "USER");

        MvcResult regResult = mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String regResponse = regResult.getResponse().getContentAsString();
        Map<?, ?> regUser = objectMapper.readValue(regResponse, Map.class);
        Long userId = ((Number) regUser.get("id")).longValue();
        assertNotNull(userId);

        // 2. Log in with the registered user
        Map<String, String> loginReq = new HashMap<>();
        loginReq.put("email", "e2e_student@test.com");
        loginReq.put("password", "studentPass123");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk());

        // 3. Create profile for the registered user
        Map<String, String> profileDto = new HashMap<>();
        profileDto.put("firstName", "E2E");
        profileDto.put("lastName", "Student");
        profileDto.put("phoneNumber", "0799999999");

        mockMvc.perform(post("/profiles/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testScenario2_AdminCategoryCourseAndLessonAddition() throws Exception {
        // 1. Create a Category
        Map<String, String> categoryDto = new HashMap<>();
        categoryDto.put("name", "E2E Cloud Computing");
        categoryDto.put("description", "All things Docker, Kubernetes, AWS.");

        MvcResult catResult = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String catResponse = catResult.getResponse().getContentAsString();
        Map<?, ?> savedCat = objectMapper.readValue(catResponse, Map.class);
        Long categoryId = ((Number) savedCat.get("id")).longValue();
        assertNotNull(categoryId);

        // 2. Create a Course under that Category
        Map<String, Object> courseDto = new HashMap<>();
        courseDto.put("title", "AWS Fundamentals");
        courseDto.put("description", "Learn Core Services");
        courseDto.put("categoryId", categoryId);

        MvcResult courseResult = mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseResponse = courseResult.getResponse().getContentAsString();
        Map<?, ?> savedCourse = objectMapper.readValue(courseResponse, Map.class);
        Long courseId = ((Number) savedCourse.get("id")).longValue();
        assertNotNull(courseId);

        // 3. Add a Lesson to the Course
        Map<String, Object> lessonDto = new HashMap<>();
        lessonDto.put("title", "1. Introducing EC2");
        lessonDto.put("contentUrl", "https://aws.amazon.com/ec2/intro");
        lessonDto.put("durationMinutes", 20);

        mockMvc.perform(post("/lessons/" + courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lessonDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testScenario3_UserEnrollmentAndReviewsRatingBounds() throws Exception {
        // Seed new category
        Map<String, String> categoryDto = new HashMap<>();
        categoryDto.put("name", "E2E Web");
        categoryDto.put("description", "Frontend/Backend");

        MvcResult catResult = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andReturn();
        Long categoryId = ((Number) objectMapper.readValue(catResult.getResponse().getContentAsString(), Map.class).get("id")).longValue();

        // Seed new course
        Map<String, Object> courseDto = new HashMap<>();
        courseDto.put("title", "NextJS Mastery");
        courseDto.put("description", "Vercel Framework");
        courseDto.put("categoryId", categoryId);

        MvcResult courseResult = mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isCreated())
                .andReturn();
        Long courseId = ((Number) objectMapper.readValue(courseResult.getResponse().getContentAsString(), Map.class).get("id")).longValue();

        // Seed user
        Map<String, String> userDto = new HashMap<>();
        userDto.put("username", "reviewer_student");
        userDto.put("email", "rev@student.com");
        userDto.put("password", "studPass");
        userDto.put("role", "USER");

        MvcResult userResult = mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn();
        Long userId = ((Number) objectMapper.readValue(userResult.getResponse().getContentAsString(), Map.class).get("id")).longValue();

        // 1. Enroll user in course
        mockMvc.perform(post("/courses/" + courseId + "/enroll/" + userId))
                .andExpect(status().isNoContent());

        // 2. Leave review with VALID rating (e.g. 5) -> 201 Created
        Map<String, Object> validReview = new HashMap<>();
        validReview.put("rating", 5);
        validReview.put("comment", "Incredible learning path!");

        mockMvc.perform(post("/reviews/" + courseId + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validReview)))
                .andExpect(status().isCreated());

        // 3. Leave review with INVALID rating too high (e.g. 6) -> 400 Bad Request
        Map<String, Object> invalidReviewHigh = new HashMap<>();
        invalidReviewHigh.put("rating", 6);
        invalidReviewHigh.put("comment", "Invalid rating test");

        mockMvc.perform(post("/reviews/" + courseId + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReviewHigh)))
                .andExpect(status().isBadRequest());

        // 4. Leave review with INVALID rating too low (e.g. 0) -> 400 Bad Request
        Map<String, Object> invalidReviewLow = new HashMap<>();
        invalidReviewLow.put("rating", 0);
        invalidReviewLow.put("comment", "Invalid rating test low");

        mockMvc.perform(post("/reviews/" + courseId + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReviewLow)))
                .andExpect(status().isBadRequest());
    }
}
