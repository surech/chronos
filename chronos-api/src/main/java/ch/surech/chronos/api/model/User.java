package ch.surech.chronos.api.model;

import lombok.*;

import java.util.EnumSet;

@Getter
@Setter
@Builder
public class User {
    private String name;
    private String email;

    private EnumSet<Weekdays> workingDays;

}
