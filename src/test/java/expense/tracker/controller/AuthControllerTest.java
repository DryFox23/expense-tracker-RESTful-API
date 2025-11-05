package expense.tracker.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import expense.tracker.entity.User;
import expense.tracker.model.LoginUserRequest;
import expense.tracker.model.TokenResponse;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private String authHeader;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void userLoginSucess() throws Exception {
        User user = new User();
        user.setEmail("email@email.com");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setUsername("username");
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("email@email.com");
        request.setPassword("password");
        request.setUsername("username");

        mockMvc.perform(post("/api/v1/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<TokenResponse>>() {
                            });

                    assertNull(response.getErrors());
                    User user1 = userRepository.findByEmail(request.getEmail()).orElse(null);
                    assertNotNull(user1.getToken());
                    System.out.println(response.getMessage());
                });
    }

    @Test
    void userLoginFailed() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("email@email.com");
        request.setPassword("password");
        request.setUsername("username");

        mockMvc.perform(post("/api/v1/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isNotFound())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });

                    assertNotNull(response.getErrors());
                    System.out.println(response.getErrors());
                });
    }

    @Test
    void userLoginFailedWrongPassword() throws Exception {
        User user = new User();
        user.setEmail("email@email.com");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setUsername("username");
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("email@email.com");
        request.setPassword("passwordd");
        request.setUsername("username");

        mockMvc.perform(post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isUnauthorized())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            });

                    assertNotNull(response.getErrors());
                    System.out.println(response.getErrors());
                });
    }

    @Test
    void userLogoutSuccess() throws Exception {
        User user = new User();
        user.setEmail("email@email.com");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setUsername("username");
        userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        authHeader = "Bearer " + token;
        user.setToken(token);
        userRepository.save(user);

        mockMvc.perform(delete("/api/v1/auth/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            });
                    assertNull(response.getErrors());
                    System.out.println(response.getMessage());
                });
    }
}
