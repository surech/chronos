package ch.surech.chronos.chronosanalyser.service;

import ch.surech.chronos.analyser.persistence.model.DistinctParticipant;
import ch.surech.chronos.analyser.persistence.repo.ParticipantRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public List<DistinctParticipant> getAllUsers(){
        List<DistinctParticipant> distinctParticipants = participantRepository.getDistinctParticipants();
        return distinctParticipants;
    }

}
