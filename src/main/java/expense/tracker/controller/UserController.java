package expense.tracker.controller;

import expense.tracker.model.RegisterUserRequest;
import expense.tracker.model.WebResponse;
import expense.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/ExpenseTask/users",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> registerUser(@RequestBody RegisterUserRequest request){
        userService.registerUser(request);
        return WebResponse.<String>builder()
                .message("Success register user")
                .data(null)
                .build();
    }
}
