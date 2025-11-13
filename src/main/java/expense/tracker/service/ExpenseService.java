package expense.tracker.service;

import expense.tracker.entity.Expense;
import expense.tracker.entity.User;
import expense.tracker.model.CreateExpenseRequest;
import expense.tracker.model.ExpenseResponse;
import expense.tracker.model.SearchExpenseRequest;
import expense.tracker.model.UpdateExpenseRequest;
import expense.tracker.repository.ExpenseRepository;
import expense.tracker.repository.UserRepository;
import expense.tracker.specification.ExpenseSpecification;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseService {
    @Autowired
    private ValidationService validationService;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserRepository userRepository;


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

        Expense expense = expenseRepository.findFirstByUserAndId(user, expenseId).orElseThrow(()->
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

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> searchExpense(User user, @Valid SearchExpenseRequest request){
        validationService.validate(request);

        User checkUser = userRepository.findById(user.getId()).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found"));

        Specification<Expense> specification = ExpenseSpecification.filterExpense(request, checkUser);

//        Sort sort = Sort.by(Sort.Direction.DESC, "created");

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize());

        Page<Expense> expenses = expenseRepository.findAll(specification, pageable);

        List<ExpenseResponse> responses = expenses.getContent()
                .stream()
                .map(this::toExpenseResponse)
                .toList();

        return new PageImpl<>(responses, pageable, expenses.getTotalElements());
    }
}
