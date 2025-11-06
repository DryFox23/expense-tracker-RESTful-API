package expense.tracker.service;

import expense.tracker.entity.User;
import expense.tracker.model.RegisterUserRequest;
import expense.tracker.model.UpdateUserRequest;
import expense.tracker.model.UserResponse;
import expense.tracker.model.WebResponse;
import expense.tracker.repository.UserRepository;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private JwtService jwtService;



    @Transactional
    public void registerUser(RegisterUserRequest request){
        validationService.validate(request);

        // cek jika emailnya telah terdaftar didalam database maka throw status UNAUTHORIZED
        // jika tidak terdaftar akan dilanjutkan pembuatan user
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
            user.setUsername(request.getUsername());
            userRepository.save(user);

    }

    @Transactional(readOnly = true)
    public UserResponse getUser(User user){

        if (user.getToken() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token is null");
        }

        if (!jwtService.validateToken(user.getToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid or expired token");
        }
        return UserResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }


    @Transactional
    public UserResponse updateUser(String userId ,UpdateUserRequest request){
        validationService.validate(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        if (Objects.nonNull(request.getEmail())) {
            user.setEmail(request.getEmail());
        }

        if (Objects.nonNull(request.getPassword()) && !request.getPassword().isBlank()) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        if (Objects.nonNull(request.getUsername())) {
            user.setUsername(request.getUsername());
        }

        userRepository.save(user);

        return UserResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }
}
