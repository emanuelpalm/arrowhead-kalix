package se.arkalix.core.plugin.dto;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * Various utilities for managing dates and times.
 */
public class Instants {
    private Instants() {}

    private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd HH:mm:ss")
        .toFormatter()
        .withZone(ZoneOffset.UTC);

    /**
     * Creates {@link Instant} from given date/time string formatted according
     * to the following pattern:
     * <pre>
     *     yyyy-MM-dd HH:mm:ss
     * </pre>
     *
     * @param dateTime String to parse.
     * @return Created {@link Instant}.
     * @throws java.time.format.DateTimeParseException If parsing fails.
     */
    public static Instant fromAitiaDateTimeString(final String dateTime) {
        return dateTimeFormatter.parse(dateTime, Instant::from);
    }
}
