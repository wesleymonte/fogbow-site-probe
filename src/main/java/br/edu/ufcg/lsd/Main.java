package br.edu.ufcg.lsd;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufcg.lsd.core.ProbeController;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	private static final int EXIT_CODE = 1;

    public static void main(String[] args) throws Exception {
		String propertiesPath = args[0];
		File propertiesSFile = new File(propertiesPath);

		if(!propertiesSFile.exists()){
			LOGGER.warn("Properties file does not exists");
			System.exit(EXIT_CODE);
		}    	
    	Properties properties = new Properties();
    	properties.load(new FileInputStream(propertiesSFile));
    	
		ProbeController probeController = new ProbeController(properties);
    	probeController.start();    
    }
}
