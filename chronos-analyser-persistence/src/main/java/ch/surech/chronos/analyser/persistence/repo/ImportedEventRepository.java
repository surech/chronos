package ch.surech.chronos.analyser.persistence.repo;

import ch.surech.chronos.analyser.persistence.model.ImportedEvent;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ImportedEventRepository extends CrudRepository<ImportedEvent, Long> {

    @Query("SELECT e.iCalUId FROM ImportedEvent e")
    List<String> getAllICalUIds();

    @Query("SELECT DISTINCT e FROM ImportedEvent e inner join fetch e.attendees a ORDER BY e.createdAt asc")
    List<ImportedEvent> getAll();

    @Query("SELECT DISTINCT e FROM ImportedEvent e inner join fetch e.attendees a WHERE e.type = 'SINGLE_INSTANCE' AND e.isAllDay = false AND e.isCancelled = false ORDER BY e.createdAt asc")
    List<ImportedEvent> getAllForExport();
}
