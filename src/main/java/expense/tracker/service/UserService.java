package expense.tracker.service;

import expense.tracker.entity.User;
import expense.tracker.model.RegisterUserRequest;
import expense.tracker.model.UpdateUserRequest;
import expense.tracker.model.UserResponse;
import expense.tracker.repository.UserRepository;
import jakarta.validation.Validator;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private Validator validator;

    @Transactional
    public void registerUser(RegisterUserRequest request){
        validationService.validate(request);

        // cek jika emailnya telah terdaftar didalam database maka throw status UNAUTHORIZED
        // jika tidak terdaftar akan dilanjutkan pembuatan user
        if (userRepository.findById(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setUsername(request.getUsername());
        userRepository.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setMessage("Successfully registered");
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(User user){
        return UserResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .message("Getting user info")
                .build();
    }


    @Transactional
    public UserResponse updateUser(User user, UpdateUserRequest request){
        validationService.validate(request);

        if (Objects.nonNull(request.getEmail())) {
            user.setEmail(request.getEmail());
        }

        if (Objects.nonNull(request.getPassword())) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        if (Objects.nonNull(request.getUsername())) {
            user.setUsername(request.getUsername());
        }

        return UserResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .message("Successfully updated user info")
                .build();
    }
}
