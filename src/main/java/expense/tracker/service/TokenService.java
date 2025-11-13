package expense.tracker.service;

import expense.tracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Jakarta")
    public void cleanExpiredTokens() {

        long now = System.currentTimeMillis();
        int updateCount = userRepository.deleteExpiredTokens(now);

        if (updateCount > 0) {
            log.info("{} Clean expired tokens", updateCount);
        } else {
            log.info("{} No expired tokens", updateCount);
        }
    }

}
