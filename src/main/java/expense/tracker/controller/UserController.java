package expense.tracker.controller;

import expense.tracker.entity.User;
import expense.tracker.model.RegisterUserRequest;
import expense.tracker.model.UpdateUserRequest;
import expense.tracker.model.UserResponse;
import expense.tracker.model.WebResponse;
import expense.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/api/v1/users",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> registerUser(@RequestBody RegisterUserRequest request){
        userService.registerUser(request);
        return WebResponse.<String>builder()
                .message("Success register user")
                .data(null)
                .build();
    }

    @GetMapping(path = "/api/v1/users/current")
    public WebResponse<UserResponse> getDataUser(User user){
        UserResponse response = userService.getUser(user);
        return WebResponse.<UserResponse>builder()
                .message("User Info")
                .data(response)
                .build();
    }

    @PatchMapping(path = "/api/v1/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> updateDataUser(@PathVariable String userId
                                                    ,@RequestBody UpdateUserRequest request){
        UserResponse response = userService.updateUser(userId, request);
        return WebResponse.<UserResponse>builder()
                .message("Update Data User")
                .data(response)
                .build();
    }
}
