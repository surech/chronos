package ch.surech.chronos.server.controller;

import ch.surech.chronos.api.model.MeetingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping(path = "/request")
public class MeetingRequestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeetingRequestController.class);

    @PostMapping
    public void addRequest(@DateTimeFormat @RequestBody MeetingRequest meetingRequest){
        LOGGER.info("Incoming Meeting-Request from {} with {} min", meetingRequest.getOrganizer(), meetingRequest.getDurationInMinutes());
    }
}
