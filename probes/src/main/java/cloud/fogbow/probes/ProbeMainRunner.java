package cloud.fogbow.probes;

import cloud.fogbow.probes.core.ProbesManager;
import cloud.fogbow.probes.core.services.DataProviderService;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ProbeMainRunner implements ApplicationRunner {

    @Autowired
    Properties properties;

    @Autowired
    DataProviderService dataProviderService;

    @Override
    public void run(ApplicationArguments applicationArguments) {
        ProbesManager.getInstance().setProperties(properties);
        ProbesManager.getInstance().init(dataProviderService);
        ProbesManager.getInstance().start();
    }
}
