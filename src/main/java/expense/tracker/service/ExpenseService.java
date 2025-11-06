package expense.tracker.service;

import expense.tracker.entity.Expense;
import expense.tracker.entity.User;
import expense.tracker.model.CreateExpenseRequest;
import expense.tracker.model.ExpenseResponse;
import expense.tracker.model.UpdateExpenseRequest;
import expense.tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ExpenseService {
    @Autowired
    private ValidationService validationService;
    @Autowired
    private ExpenseRepository expenseRepository;


    @Transactional
    public ExpenseResponse createExpense(User user, CreateExpenseRequest request){
        validationService.validate(request);

        Expense expense = new Expense();
        expense.setId(UUID.randomUUID().toString());
        expense.setUser(user);
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expense.setType(request.getType());
        expense.setCategory(request.getCategory());
        expense.setPaymentMethod(request.getPaymentMethod());
        expenseRepository.save(expense);

        return toExpenseResponse(expense);
    }

    private ExpenseResponse toExpenseResponse(Expense expense){
        return ExpenseResponse.builder()
                .id(expense.getId())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .date(expense.getDate())
                .type(expense.getType())
                .category(expense.getCategory())
                .paymentMethod(expense.getPaymentMethod())
                .build();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(User user,String expenseId){

        Expense expense = expenseRepository.findFirstByUserAndId(user, expenseId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));

        return toExpenseResponse(expense);
    }

    @Transactional
    public ExpenseResponse updateExpense(User user, UpdateExpenseRequest request, String expenseId){
        validationService.validate(request);

        Expense expense = expenseRepository.findFirstByUserAndId(user, request.getId()).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "expense not found"));



        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expense.setType(request.getType());
        expense.setCategory(request.getCategory());
        expense.setPaymentMethod(request.getPaymentMethod());
        expenseRepository.save(expense);

        return toExpenseResponse(expense);
    }

    @Transactional
    public void deleteExpense(User user, String expenseId){

        Expense expense = expenseRepository.findFirstByUserAndId(user, expenseId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "expense not found")
        );

        expenseRepository.delete(expense);
    }
}
