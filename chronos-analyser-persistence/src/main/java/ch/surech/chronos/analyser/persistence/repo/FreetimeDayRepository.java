package ch.surech.chronos.analyser.persistence.repo;

import ch.surech.chronos.analyser.persistence.model.FreetimeDay;
import ch.surech.chronos.analyser.persistence.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface FreetimeDayRepository extends CrudRepository<FreetimeDay, Long> {
}
