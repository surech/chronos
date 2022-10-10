package ch.surech.chronos.analyser.persistence.repo;

import ch.surech.chronos.analyser.persistence.model.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonRepository extends CrudRepository<Person, Long> {

    @Query("SELECT p.graphId FROM Person p")
    List<String> getAllGraphIds();
}
