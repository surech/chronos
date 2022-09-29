package ch.surech.chronos.leecher.mapper;

import ch.surech.chronos.analyser.persistence.model.Participant;
import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.Recipient;

public final class ParticipantMapper {

    private final static String EMPTY_NAME = "<Unknown>";
    private final static String EMPTY_ADRESS = "unknown@example.com";

    private ParticipantMapper() {
    }

    public static Participant toParticipant(Recipient recipient) {
        Participant.ParticipantBuilder builder = Participant.builder();
        extractEMail(recipient, builder);

        return builder.build();
    }

    public static Participant toParticipant(Attendee attendee) {
        Participant.ParticipantBuilder builder = Participant.builder();

        extractEMail(attendee, builder);
        builder.attendeeType(attendee.type);
        if(attendee.status != null) {
            builder.responseStatus(attendee.status.response);
        }
        return builder.build();
    }

    private static void extractEMail(Recipient recipient, Participant.ParticipantBuilder builder) {

        if (recipient != null && recipient.emailAddress != null) {
            builder.name(recipient.emailAddress.name);
            builder.address(recipient.emailAddress.address);
        } else {
            builder.name(EMPTY_NAME);
            builder.address(EMPTY_ADRESS);
        }
    }
}
