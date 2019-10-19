package ch.surech.chronos.api.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Invitee {

    private String email;

    private boolean optional = false;
}
