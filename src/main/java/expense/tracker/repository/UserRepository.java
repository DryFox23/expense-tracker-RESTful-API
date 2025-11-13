package expense.tracker.repository;

import expense.tracker.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.token = null, u.token = null where u.token < :now")
    int deleteExpiredTokens(@Param("now") Long now);
}
