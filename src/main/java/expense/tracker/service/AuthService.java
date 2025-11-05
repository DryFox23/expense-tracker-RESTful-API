package expense.tracker.service;

import expense.tracker.entity.User;
import expense.tracker.model.LoginUserRequest;
import expense.tracker.model.TokenResponse;
import expense.tracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public TokenResponse loginUser(LoginUserRequest request){
        validationService.validate(request);

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        if (BCrypt.checkpw(request.getPassword(), user.getPassword())){
            user.setToken(token);
            userRepository.save(user);
            return TokenResponse.builder()
                    .token(user.getToken())
                    .build();
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect");
        }

    }


    @Transactional
    public void logout(User user){

        if (user.getToken() == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User already logged out");
        }

        if (!jwtService.validateToken(user.getToken())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        user.setToken(null);
        userRepository.save(user);
    }
}
