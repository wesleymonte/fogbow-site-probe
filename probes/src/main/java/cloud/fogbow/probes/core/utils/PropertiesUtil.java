package cloud.fogbow.probes.core.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

    public static Properties loadProperties(String fileName) throws Exception {
        Properties prop = new Properties();
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(fileName);
            prop.load(fileInputStream);
        } catch (FileNotFoundException var12) {
            throw new Exception(String.format("Property file %s not found.", fileName), var12);
        } catch (IOException var13) {
            throw new Exception(var13.getMessage(), var13);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return prop;
    }
}
