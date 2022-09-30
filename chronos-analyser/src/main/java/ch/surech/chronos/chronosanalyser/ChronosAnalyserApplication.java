package ch.surech.chronos.chronosanalyser;

import ch.surech.chronos.analyser.persistence.model.DistinctParticipant;
import ch.surech.chronos.chronosanalyser.service.ParticipantService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"ch.surech.chronos.analyser.persistence.repo"})
@EntityScan(basePackages = {"ch.surech.chronos.analyser.persistence.model"})
public class ChronosAnalyserApplication implements CommandLineRunner {

	@Autowired
	private ParticipantService userService;

	private final static Logger LOGGER = LoggerFactory
		.getLogger(ChronosAnalyserApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ChronosAnalyserApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<DistinctParticipant> allUsers = userService.getAllUsers();
		for (int i = 0; i < allUsers.size(); i++) {
			DistinctParticipant user = allUsers.get(i);
			LOGGER.info("[{}] {} [{}]", i, user.getName(), user.getAddress());
		}
	}
}
