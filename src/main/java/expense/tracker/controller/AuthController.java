package expense.tracker.controller;

import expense.tracker.entity.User;
import expense.tracker.model.LoginUserRequest;
import expense.tracker.model.TokenResponse;
import expense.tracker.model.WebResponse;
import expense.tracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/api/v1/auth/login",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<TokenResponse> loginUser(@RequestBody LoginUserRequest request){
        TokenResponse tokenResponse = authService.loginUser(request);
        return WebResponse.<TokenResponse>builder()
                .message("Sucess Login")
                .data(tokenResponse)
                .build();
    }

    @DeleteMapping(path = "/api/v1/auth/logout",
    produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> logoutUser(User user){
        authService.logout(user);
        return WebResponse.<String>builder()
                .message("Success Logout")
                .data("success")
                .build();
    }
}
