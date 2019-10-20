package ch.surech.chronos.server.repo;

import ch.surech.chronos.server.entities.UserPrecentePreferenceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPrecentePreferenceRepository extends CrudRepository<UserPrecentePreferenceEntity, Long> {

    @Query("SELECT p FROM UserPrecentePreferenceEntity p JOIN p.user u WHERE p.id = :id AND u.email = :email")
    UserPrecentePreferenceEntity findByIdAndEMail(@Param("id") Long id, @Param("email") String email);

    @Query("SELECT p FROM UserPrecentePreferenceEntity p JOIN p.user u WHERE u.email = :email")
    List<UserPrecentePreferenceEntity> findByEMail(@Param("email") String email);
}
