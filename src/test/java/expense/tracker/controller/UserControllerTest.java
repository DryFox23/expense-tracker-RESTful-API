package expense.tracker.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import expense.tracker.entity.User;
import expense.tracker.model.RegisterUserRequest;
import expense.tracker.model.UpdateUserRequest;
import expense.tracker.model.UserResponse;
import expense.tracker.model.WebResponse;
import expense.tracker.repository.UserRepository;
import expense.tracker.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String token;
    private String authHeader;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerUserSuccess() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("email@example.com");
        request.setPassword("password");
        request.setUsername("username");

        mockMvc.perform(post("/ExpenseTask/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            });

                    assertNull(response.getErrors());
                    System.out.println(response.getMessage());
                });
    }

    @Test
    void registerUserFailed() throws Exception {
        User user = new User();
        user.setEmail("email@example.com");
        user.setUsername("username");
        user.setPassword("password");
        userRepository.save(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("email@example.com");
        request.setPassword("password");
        request.setUsername("username");

        mockMvc.perform(post("/ExpenseTask/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isBadRequest())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            });

                    assertNotNull(response.getErrors());
                    System.out.println(response.getErrors());
                });
    }

    @Test
    void getUserInfoSuccess() throws Exception {
        User user = new User();
        user.setEmail("email@example.com");
        user.setUsername("username");
        user.setPassword("password");
        userRepository.save(user);

        token = jwtService.generateToken(user.getId(), user.getEmail());
        authHeader = "Bearer " + token;

        mockMvc.perform(get("/api/v1/users/current")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<UserResponse>>() {
                            });

                    assertNull(response.getErrors());
                    assertEquals(response.getData().getEmail(), user.getEmail());
                    assertEquals(response.getData().getUsername(), user.getUsername());
                    System.out.println(response.getMessage());
                });
    }

    @Test
    void getUserInfoFailed() throws Exception {

        User user = new User();
        user.setEmail("email@example.com");
        user.setUsername("username");
        user.setPassword("password");
        userRepository.save(user);

        token = jwtService.generateToken(user.getId(), user.getEmail());
        authHeader = "Bearerr " + token;

        mockMvc.perform(get("/api/v1/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authHeader))
                .andExpectAll(status().isUnauthorized())
                .andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<UserResponse>>() {
                            });

                    assertNotNull(response.getErrors());
                    System.out.println(response.getErrors());
                });
    }

    @Test
    void updateDataUserSuccess() throws Exception {
        User user = new User();
        user.setEmail("email@example.com");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setUsername("username");
        userRepository.save(user);

        token = jwtService.generateToken(user.getId(), user.getEmail());
        authHeader = "Bearer " + token;

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("update@example.com");
        request.setPassword("update");
        request.setUsername("username");

        mockMvc.perform(patch("/api/v1/users/" + user.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", authHeader))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<UserResponse>>() {
                            });
                    assertNull(response.getErrors());
                    assertNotEquals(response.getData().getEmail(), user.getEmail());
                    assertEquals(response.getData().getUsername(), user.getUsername());

                    User userDb = userRepository.findByEmail("update@example.com").orElse(null);
                    assertTrue(BCrypt.checkpw("update", userDb.getPassword()));
                    System.out.println(response.getMessage());
                });
    }
}
