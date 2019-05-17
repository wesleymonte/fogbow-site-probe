package cloud.fogbow.probes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;

@SpringBootApplication()
public class FogbowProbesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FogbowProbesApplication.class, args);
    }
}
