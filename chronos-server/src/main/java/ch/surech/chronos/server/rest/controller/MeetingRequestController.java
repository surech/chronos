package ch.surech.chronos.server.rest.controller;

import ch.surech.chronos.api.model.Event;
import ch.surech.chronos.api.model.MeetingRequest;
import ch.surech.chronos.server.entities.EventEntity;
import ch.surech.chronos.server.mapper.EventMapper;
import ch.surech.chronos.server.service.FindMeetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping(path = "/request")
public class MeetingRequestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeetingRequestController.class);

    @Autowired
    private FindMeetingService findMeetingService;

    @Autowired
    private EventMapper eventMapper;

    @PostMapping
    public Event addRequest(@DateTimeFormat @RequestBody MeetingRequest meetingRequest){
        LOGGER.info("Incoming Meeting-Request from {} with {} min", meetingRequest.getOrganizer(), meetingRequest.getDurationInMinutes());

        LocalDateTime start = LocalDateTime.now();
        EventEntity event = findMeetingService.search(meetingRequest);
        Duration duration = Duration.between(start, LocalDateTime.now());
        LOGGER.info("Meeting found in {} ms", duration.get(ChronoUnit.MILLIS));
        return eventMapper.fromEntity(event);
    }
}
