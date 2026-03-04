package net.bjmsw.uvm.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeUtils {

    /**
     * Converts a datetime-local string from an HTML form into a Unix epoch timestamp (in seconds),
     * respecting the server's local timezone (e.g., Europe/Berlin).
     *
     * @param datetimeLocal the string from the form, e.g., "2026-03-03T14:30"
     * @return the Unix epoch timestamp in seconds, or -1 if parsing fails
     */
    public static long formToEpochSeconds(String datetimeLocal) {
        if (datetimeLocal == null || datetimeLocal.trim().isEmpty()) {
            throw new IllegalArgumentException("Datetime string cannot be null or empty");
        }

        try {
            LocalDateTime localTime = LocalDateTime.parse(datetimeLocal);

            // Note: Ensure your Docker container / Proxmox host is set to "Europe/Berlin"
            ZonedDateTime zonedTime = localTime.atZone(ZoneId.systemDefault());

            return zonedTime.toEpochSecond();

        } catch (DateTimeParseException e) {
            System.err.println("[TimeUtils] Failed to parse HTML datetime: " + datetimeLocal);
            throw new IllegalArgumentException("Invalid datetime format: " + datetimeLocal, e);
        }
    }

    /**
     * Converts a Unix epoch timestamp (in seconds) back into a String for the Admins in the Console UI
     * Format: DDMMYY_HHMMSS
     *
     * @return the formatted datetime string, or null if the input is invalid
     */
    public static String buildInternalDateTimeString(long epochSeconds) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy_HHmmss");
        return dateTime.format(formatter);
    }
}