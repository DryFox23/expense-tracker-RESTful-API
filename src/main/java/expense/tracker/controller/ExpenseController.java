package expense.tracker.controller;

import expense.tracker.entity.User;
import expense.tracker.model.CreateExpenseRequest;
import expense.tracker.model.ExpenseResponse;
import expense.tracker.model.UpdateExpenseRequest;
import expense.tracker.model.WebResponse;
import expense.tracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
}
