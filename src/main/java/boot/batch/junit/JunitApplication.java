package boot.batch.junit;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class JunitApplication {

    public static void main(String[] args) {
        SpringApplication.run(JunitApplication.class, args);
    }

}
