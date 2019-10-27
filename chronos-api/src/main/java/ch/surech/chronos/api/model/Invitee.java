package ch.surech.chronos.api.model;

import lombok.*;

@Getter
@Setter
@Builder
public class Invitee {

    private String email;

    private boolean optional = false;
}
