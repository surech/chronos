package ch.surech.chronos.api.model;

import lombok.*;

@Getter
@Setter
@Builder
public class User {
    private String name;
    private String email;
}
