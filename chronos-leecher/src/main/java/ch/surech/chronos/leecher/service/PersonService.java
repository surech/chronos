package ch.surech.chronos.leecher.service;

import ch.surech.chronos.analyser.persistence.model.Person;
import ch.surech.chronos.analyser.persistence.repo.PersonRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

    private final static String NAME_ORG_REGEX = ".+\\((?<org>\\w+(-\\w+)*)(?<ext> - Extern)?\\)$";

    private final static Pattern NAME_ORG_PATTERN = Pattern.compile(NAME_ORG_REGEX);

    @Autowired
    private PersonRepository personRepository;

    private final Set<String> existingUserIds = new HashSet<>();

    @PostConstruct
    private void init(){
        // Pre-Load all existing IDs from the database
        List<String> allGraphIds = personRepository.getAllGraphIds();
        existingUserIds.addAll(allGraphIds);
    }

    public boolean exists(Person person){
        return existingUserIds.contains(person.getGraphId());
    }

    public String extractOrganisationFromName(String displayName){
        Matcher matcher = NAME_ORG_PATTERN.matcher(displayName);
        if(matcher.find()) {
            return matcher.group("org");
        } else {
            LOGGER.warn("No valid name found: {}", displayName);
            return null;
        }
    }

    public boolean isExternal(String displayName){
        Matcher matcher = NAME_ORG_PATTERN.matcher(displayName);

        if(matcher.find()) {
            String ext = matcher.group("ext");
            return ext != null;
        } else {
            LOGGER.warn("No valid name found: {}", displayName);
            return false;
        }
    }

    public Person save(Person person){
        // Check, if person allready exists
        if (existingUserIds.contains(person.getGraphId())) {
            LOGGER.info("Person with ID {} allready exists and will not stored again", person.getGraphId());
            return person;
        }

        // Save person
        Person savedPerson = personRepository.save(person);

        // Add to our set for not storing again
        existingUserIds.add(savedPerson.getGraphId());
        return savedPerson;
    }
}
