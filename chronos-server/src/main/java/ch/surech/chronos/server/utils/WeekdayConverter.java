package ch.surech.chronos.server.utils;

import ch.surech.chronos.api.model.Weekdays;

import javax.persistence.AttributeConverter;
import java.util.EnumSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class WeekdayConverter implements AttributeConverter<EnumSet<Weekdays>, String> {

    public static final String SEPERATOR = ",";

    @Override
    public String convertToDatabaseColumn(EnumSet<Weekdays> weekdays) {
        StringBuilder sb = new StringBuilder();
        for (Weekdays weekday : weekdays) {
            sb.append(weekday.name());
            sb.append(SEPERATOR);
        }
        return sb.toString();
    }

    @Override
    public EnumSet<Weekdays> convertToEntityAttribute(String dbValue) {
        EnumSet<Weekdays> result = EnumSet.noneOf(Weekdays.class);
        if(dbValue != null) {
            Stream.of(dbValue.split(SEPERATOR)).filter(Predicate.not(String::isBlank)).map(Weekdays::valueOf).forEach(result::add);
        }
        return result;
    }
}
