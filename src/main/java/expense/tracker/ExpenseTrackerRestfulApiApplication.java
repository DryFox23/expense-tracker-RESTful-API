package expense.tracker;

import expense.tracker.configuration.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
@EnableScheduling
public class ExpenseTrackerRestfulApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseTrackerRestfulApiApplication.class, args);
	}

}
