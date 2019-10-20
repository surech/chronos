package ch.surech.chronos.server.rest.controller;

import ch.surech.chronos.api.model.User;
import ch.surech.chronos.api.model.UserPrecentePreference;
import ch.surech.chronos.server.entities.UserEntity;
import ch.surech.chronos.server.entities.UserPrecentePreferenceEntity;
import ch.surech.chronos.server.mapper.UserMapper;
import ch.surech.chronos.server.mapper.UserPrecentePreferenceMapper;
import ch.surech.chronos.server.repo.UserPrecentePreferenceRepository;
import ch.surech.chronos.server.repo.UserRepository;
import ch.surech.chronos.server.rest.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPrecentePreferenceRepository userPrecentePreferenceRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserPrecentePreferenceMapper userPrecentePreferenceMapper;

    @PostMapping
    public User createUser(@RequestBody User user){
        // Create Entity
        UserEntity userEntity = userMapper.toEntity(user);

        // Save
        UserEntity savedEntity = userRepository.save(userEntity);

        return userMapper.fromEntity(savedEntity);
    }

    @GetMapping(path = "/{email}")
    public User findByEMail(@PathVariable("email") String email){
        UserEntity user = userRepository.findByEmail(email);
        if(user == null){
            throw new NotFoundException("No user with EMail " + email + " found");
        } else {
            return userMapper.fromEntity(user);
        }
    }

    @PostMapping(path = "/{email}/precencePreference")
    public UserPrecentePreference createPrecentePreference(@PathVariable String email, @RequestBody UserPrecentePreference preference){
        // Find User
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("No user with EMail " + email + " found");
        }

        UserPrecentePreferenceEntity entity = userPrecentePreferenceMapper.toEntity(preference, user);
        UserPrecentePreferenceEntity savedEntity = userPrecentePreferenceRepository.save(entity);

        return userPrecentePreferenceMapper.fromEntity(savedEntity);
    }

    @GetMapping(path = "/{email}/precencePreference")
    public List<UserPrecentePreference> getAllForUser(@PathVariable String email){
        List<UserPrecentePreferenceEntity> preferences = userPrecentePreferenceRepository.findByEMail(email);
        List<UserPrecentePreference> result = preferences.stream().map(userPrecentePreferenceMapper::fromEntity).collect(Collectors.toList());
        return result;
    }

    @GetMapping(path = "/{email}/precencePreference/{id}")
    public UserPrecentePreference getPrecentePreference(@PathVariable String email, @PathVariable Long id){
        UserPrecentePreferenceEntity preference = userPrecentePreferenceRepository.findByIdAndEMail(id, email);

        if (preference == null) {
            throw new NotFoundException("No preference found");
        } else {
            return userPrecentePreferenceMapper.fromEntity(preference);
        }
    }
}
