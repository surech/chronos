package ch.surech.chronos.chronosanalyser;

import ch.surech.chronos.chronosanalyser.service.AuthentificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChronosAnalyserApplication implements CommandLineRunner {

	@Autowired
	private AuthentificationService authentificationService;

	private final static Logger LOGGER = LoggerFactory
		.getLogger(ChronosAnalyserApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ChronosAnalyserApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("Start...");
		authentificationService.makeConnection();
	}
}
