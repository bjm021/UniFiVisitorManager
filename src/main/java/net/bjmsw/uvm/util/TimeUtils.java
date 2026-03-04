package net.bjmsw.uvm.util;

import net.bjmsw.uvm.VisitorManager;
import net.bjmsw.uvm.model.Visitor;

import java.time.Instant;
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
    public static long formStringToEpochSeconds(String datetimeLocal) {
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
     * Converts a Unix epoch timestamp (in seconds) into a formatted datetime string in the server's local timezone (e.g., Europe/Berlin) using the specified format pattern.
     *
     * @param epochSeconds the Unix epoch time in seconds to be converted
     * @param format the datetime format pattern to use for conversion
     * @return the formatted datetime string representing the specified epoch time
     */
    public static String fromEpochSecondsToDateTimeString(long epochSeconds, String format) {
        Instant instant = Instant.ofEpochSecond(epochSeconds);

        ZoneId zone = ZoneId.of(VisitorManager.getSettings().getOrDefault("timezone", "Europe/Berlin"));
        ZonedDateTime zdt = instant.atZone(zone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return zdt.format(formatter);
    }

    /**
     * Converts an epoch timestamp in seconds to a formatted datetime string using a default pattern.
     *
     * @param epochSeconds the Unix epoch time in seconds to be converted
     * @return the formatted datetime string using the default pattern "yyyy-MM-dd HH:mm"
     */
    public static String fromEpochSecondsToDateTimeString(long epochSeconds) {
        return fromEpochSecondsToDateTimeString(epochSeconds, "yyyy-MM-dd HH:mm");
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

    /**
     * Retrieves the current year as a string.
     *
     * @return the current year in the format of a four-digit string (e.g., "2023").
     */
    public static String getCurrentYear() {
        return LocalDateTime.now().getYear() + "";
    }

    /**
     * Formats a date range given the start and end Unix epoch timestamps in seconds
     * into a human-readable string representation. The output is formatted in
     * the "yyyy-MM-dd HH:mm" pattern and uses the "Europe/Berlin" timezone.
     *
     * @param start_time the Unix epoch timestamp in seconds representing the start of the range
     * @param end_time the Unix epoch timestamp in seconds representing the end of the range
     * @return a human-readable string representation of the date range in the format
     *         "start to end", where start and end are formatted datetime strings
     */
    public static String getPrettyDateRange(long start_time, long end_time) {
        ZoneId siteZone = ZoneId.of(VisitorManager.getSettings().getOrDefault("timezone", "Europe/Berlin"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String start = formatter.format(
                java.time.Instant.ofEpochSecond(start_time)
                        .atZone(siteZone)
        );

        String end = formatter.format(
                java.time.Instant.ofEpochSecond(end_time)
                        .atZone(siteZone)
        );

        return start + " to " + end;
    }

    /**
     * Gets the current time in Epoch Seconds (UTC).
     * This is the exact format the UniFi Access API uses.
     */
    public static long getCurrentUniFiTime() {
        return Instant.now().getEpochSecond();
    }

    /**
     * Checks if a UniFi visitor's access has expired.
     * @param unifiEndTime The end_time from the UniFi API (in seconds)
     * @return true if the current time is past the end time
     */
    public static boolean isVisitorExpired(Visitor v) {
        long currentTimeSeconds = getCurrentUniFiTime();
        return currentTimeSeconds > v.getEnd_time();
    }

}