package ch.surech.chronos.leecher.repo;

import ch.surech.chronos.leecher.model.DistinctParticipant;
import ch.surech.chronos.leecher.model.Participant;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ParticipantRepository extends CrudRepository<Participant, Long> {

    @Query("SELECT distinct new ch.surech.chronos.leecher.model.DistinctParticipant(p.name, p.address) FROM Participant p")
    List<DistinctParticipant> getDistinctParticipants();

}
