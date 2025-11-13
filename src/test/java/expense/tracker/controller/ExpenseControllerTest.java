package expense.tracker.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import expense.tracker.entity.*;
import expense.tracker.model.CreateExpenseRequest;
import expense.tracker.model.ExpenseResponse;
import expense.tracker.model.UpdateExpenseRequest;
import expense.tracker.model.WebResponse;
import expense.tracker.repository.ExpenseRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    String token;
    String authHeader;
    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("email@email.com");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setUsername("username");
        userRepository.save(user);

        token = jwtService.generateToken(user.getId(), user.getEmail());
        authHeader = "Bearer " + token;
        userRepository.save(user);
    }

    @Test
    void createExpenseSuccess() throws Exception {

        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setTitle("Title");
        request.setDescription("Description");
        request.setAmount(new BigDecimal(50000.00));
        request.setDate(LocalDate.of(2025, 12, 12));
        request.setType(ExpenseType.EXPENSE);
        request.setCategory(ExpenseCategory.TRANSPORT);
        request.setPaymentMethod(ExpensePaymentMethod.EWALLET);

        mockMvc.perform(post("/api/v1/expenses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", authHeader))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<ExpenseResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<ExpenseResponse>>() {
                            });
                    assertNull(response.getErrors());
                    System.out.println(response.getMessage());
                    System.out.println(response.getData());
                });
    }

    @Test
    void createExpenseFailed() throws Exception {

        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setTitle("");
        request.setDescription("Description");
        request.setAmount(new BigDecimal(50000.00));
        request.setDate(LocalDate.of(2025, 12, 12));
        request.setType(ExpenseType.EXPENSE);
        request.setCategory(ExpenseCategory.TRANSPORT);
        request.setPaymentMethod(ExpensePaymentMethod.EWALLET);

        mockMvc.perform(post("/api/v1/expenses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", authHeader))
                .andExpectAll(status().isBadRequest())
                .andDo(result -> {
                    WebResponse<ExpenseResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<ExpenseResponse>>() {
                            });
                    assertNotNull(response.getErrors());
                    System.out.println(response.getErrors());
                });
    }

    @Test
    void getExpenseSuccess() throws Exception {
        User user = userRepository.findByEmail("email@email.com").orElse(null);

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setId(UUID.randomUUID().toString());
        expense.setTitle("Title");
        expense.setDescription("Description");
        expense.setAmount(new BigDecimal(50000.00));
        expense.setDate(LocalDate.of(2025, 12, 12));
        expense.setType(ExpenseType.EXPENSE);
        expense.setCategory(ExpenseCategory.TRANSPORT);
        expense.setPaymentMethod(ExpensePaymentMethod.EWALLET);
        expenseRepository.save(expense);

        mockMvc.perform(get("/api/v1/expenses/" + expense.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<ExpenseResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<ExpenseResponse>>() {
                            });

                    assertNull(response.getErrors());
                    System.out.println(response.getMessage());
                    System.out.println(response.getData());
                });
    }

    @Test
    void getExpensesFailed() throws Exception {

        Expense expense = new Expense();
        expense.setId(UUID.randomUUID().toString());
        expense.setTitle("Title");
        mockMvc.perform(get("/api/v1/expenses/" + expense.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authHeader))
                .andExpectAll(status().isNotFound())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            });
                    assertNotNull(response.getErrors());
                    System.out.println(response.getErrors());
                });
    }

    @Test
    void updateExpenseSuccess() throws Exception {
        User user = userRepository.findByEmail("email@email.com").orElse(null);

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setId(UUID.randomUUID().toString());
        expense.setTitle("Title");
        expense.setDescription("Description");
        expense.setAmount(new BigDecimal(50000.00));
        expense.setDate(LocalDate.of(2025, 12, 12));
        expense.setType(ExpenseType.EXPENSE);
        expense.setCategory(ExpenseCategory.TRANSPORT);
        expense.setPaymentMethod(ExpensePaymentMethod.EWALLET);
        expenseRepository.save(expense);

        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setTitle("Title updated");
        request.setDescription("Description updated");

        mockMvc.perform(patch("/api/v1/expenses/update/" + expense.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", authHeader))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<ExpenseResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<ExpenseResponse>>() {
                            });
                    assertNull(response.getErrors());
                    System.out.println(response.getData());
                    System.out.println(response.getMessage());
                });
    }

    @Test
    void updateExpenseFailed() throws Exception {
        User user = userRepository.findByEmail("email@email.com").orElse(null);

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setId(UUID.randomUUID().toString());
        expense.setTitle("Title");

        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setTitle("Title updated");
        request.setDescription("Description updated");

        mockMvc.perform(patch("/api/v1/expenses/update/" + expense.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", authHeader))
                .andExpectAll(status().isNotFound())
                .andDo(result -> {
                    WebResponse<ExpenseResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<ExpenseResponse>>() {
                            });
                    assertNotNull(response.getErrors());
                    System.out.println(response.getMessage());
                });
    }

    @Test
    void deleteExpenseSuccess() throws Exception {
        User user = userRepository.findByEmail("email@email.com").orElse(null);

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setId(UUID.randomUUID().toString());
        expense.setTitle("Title");
        expense.setDescription("Description");
        expense.setAmount(new BigDecimal(50000.00));
        expense.setDate(LocalDate.of(2025, 12, 12));
        expense.setType(ExpenseType.EXPENSE);
        expense.setCategory(ExpenseCategory.TRANSPORT);
        expense.setPaymentMethod(ExpensePaymentMethod.EWALLET);
        expenseRepository.save(expense);

        mockMvc.perform(delete("/api/v1/expenses/remove/" + expense.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            });
                    assertNull(response.getErrors());
                    System.out.println(response.getMessage());
                    System.out.println(response.getData());

                    Expense cekExpense = expenseRepository.existsById(expense.getId()) ? expense : null;
                    assertNull(cekExpense);
                });
    }

    @Test
    void searchAllExpenses() throws Exception {
        User user = userRepository.findByEmail("email@email.com").orElse(null);

        for (int i = 0; i < 5; i++) {
            Expense expense = new Expense();
            expense.setUser(user);
            expense.setId(UUID.randomUUID().toString());
            expense.setTitle("Title" + i);
            expense.setDescription("Description");
            expense.setAmount(new BigDecimal(50000.00));
            expense.setDate(LocalDate.of(2025, 12, 12));
            expense.setType(ExpenseType.EXPENSE);
            expense.setCategory(ExpenseCategory.TRANSPORT);
            expense.setPaymentMethod(ExpensePaymentMethod.EWALLET);
            expenseRepository.save(expense);
        }

        mockMvc.perform(get("/api/v1/expenses")
        .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<List<ExpenseResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<List<ExpenseResponse>>>() {
                            });
                    assertNull(response.getErrors());
                    System.out.println(response.getMessage());
                    System.out.println(response.getData());
                    assertEquals(5, response.getData().size());
                    assertEquals(10, response.getPagingResponse().getTotalSize());
                    assertEquals(0, response.getPagingResponse().getCurrentPage());
                });
    }

    @Test
    void searchAllExpensesFailed() throws Exception {

        mockMvc.perform(get("/api/v1/expenses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authHeader))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<List<ExpenseResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<List<ExpenseResponse>>>() {
                            });
                    assertNotNull(response.getErrors());
                    System.out.println(response.getErrors());
                });
    }
}
