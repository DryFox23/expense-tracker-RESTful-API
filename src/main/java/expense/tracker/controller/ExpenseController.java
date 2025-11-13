package expense.tracker.controller;

import expense.tracker.entity.ExpenseCategory;
import expense.tracker.entity.ExpenseType;
import expense.tracker.entity.User;
import expense.tracker.model.*;
import expense.tracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping(path = "/api/v1/expenses",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ExpenseResponse> createExpense(User user, @RequestBody CreateExpenseRequest request){
        ExpenseResponse expenseResponse = expenseService.createExpense(user, request);
        return WebResponse.<ExpenseResponse>builder()
                .message("success create expense")
                .data(expenseResponse)
                .build();
    }

    @GetMapping(path = "/api/v1/expenses/{expenseId}",
    produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ExpenseResponse> getExpenseById(User user, @PathVariable String expenseId){
        ExpenseResponse response = expenseService.getExpenseById(user, expenseId);
        return WebResponse.<ExpenseResponse>builder()
                .message("get expense info")
                .data(response)
                .build();
    }

    @PatchMapping(path = "/api/v1/expenses/update/{expenseId}",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ExpenseResponse> updateExpense(User user,
                                                      @RequestBody UpdateExpenseRequest request,
                                                      @PathVariable String expenseId){
        ExpenseResponse response = expenseService.updateExpense(user,request,expenseId);
        return WebResponse.<ExpenseResponse>builder()
                .message("update expense info")
                .data(response)
                .build();
    }

    @DeleteMapping(path = "/api/v1/expenses/remove/{expenseId}",
    consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> deleteExpense(User user, @PathVariable String expenseId){
        expenseService.deleteExpense(user, expenseId);
        return WebResponse.<String>builder()
                .message("delete expense")
                .data("success delete expense")
                .build();
    }

    @GetMapping(path = "/api/v1/expenses",
    produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<ExpenseResponse>> searchExpense(User user,
                                                            @RequestParam (value = "title", required = false) String title,
                                                            @RequestParam (value = "amount", required = false)BigDecimal amount,
                                                            @RequestParam (value = "type", required = false)ExpenseType type,
                                                            @RequestParam (value = "category", required = false) ExpenseCategory category,
                                                            @RequestParam (value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                                            @RequestParam (value = "pageSize", required = false, defaultValue = "10") Integer pageSize){

        SearchExpenseRequest request = SearchExpenseRequest.builder()
                .title(title)
                .amount(amount)
                .category(category)
                .expenseType(type)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        Page<ExpenseResponse> expensePage = expenseService.searchExpense(user, request);
        return WebResponse.<List<ExpenseResponse>>builder()
                .message("search expense")
                .errors("expense not found")
                .data(expensePage.getContent())
                .pagingResponse(PagingResponse.builder()
                        .currentPage(expensePage.getNumber())
                        .totalPages(expensePage.getTotalPages())
                        .totalSize(expensePage.getSize())
                        .build())
                .build();
    }
}
