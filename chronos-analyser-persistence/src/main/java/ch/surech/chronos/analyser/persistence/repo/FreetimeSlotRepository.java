package ch.surech.chronos.analyser.persistence.repo;

import ch.surech.chronos.analyser.persistence.model.FreetimeSlot;
import ch.surech.chronos.analyser.persistence.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface FreetimeSlotRepository extends CrudRepository<FreetimeSlot, Long> {
}
