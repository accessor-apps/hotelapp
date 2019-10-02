package accessor.hotel;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Util {
    static final int MINUTES_PER_HOUR = 60;
    static final int SECONDS_PER_MINUTE = 60;
    static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

    public static String datetimeToString(LocalDateTime dateTime) {
        StringBuilder builder = new StringBuilder();
        appendNumber(builder, dateTime.getDayOfMonth()).append("/");
        appendNumber(builder, dateTime.getMonthValue()).append("/");
        appendNumber(builder, dateTime.getYear()).append(":");
        appendNumber(builder, dateTime.getHour()).append(".");
        appendNumber(builder,dateTime.getMinute());
        return builder.toString();
    }
    
    public static String datetimeToName(LocalDateTime dateTime) {
        StringBuilder builder = new StringBuilder();
        appendNumber(builder, dateTime.getDayOfMonth()).append("-");
        appendNumber(builder, dateTime.getMonthValue()).append("-");
        appendNumber(builder, dateTime.getYear()).append("T");
        appendNumber(builder, dateTime.getHour()).append("-");
        appendNumber(builder,dateTime.getMinute());
        return builder.toString();
    }
    
    private static StringBuilder appendNumber(StringBuilder builder, int number) {
        if (number < 10) builder.append("0");
        builder.append(number);
        return builder;
    }
    
    public static int differenceOfDays(LocalDateTime startDate, LocalDateTime enDateTime) {
        long seconds = Duration.between(startDate, enDateTime).getSeconds();
        int days = (int) (seconds / 60 / 60 / 24);
        return days;
    }
    
    public static LocalDateTime datetimeFromString(String value) {
        
        int day = 1, month = 1, year = 1;
        int hour = 0, minute = 0;
        
        // 1/10/2019:12.04
        int monthSlashPos = value.indexOf("/");
        day = Integer.parseInt(value.substring(0, monthSlashPos));
        monthSlashPos++;

        int yearSlashPos = value.indexOf("/", monthSlashPos);
        month = Integer.parseInt(value.substring(monthSlashPos, yearSlashPos));
        yearSlashPos++;

        int timeColonPos = value.indexOf(":", yearSlashPos);
        year = Integer.parseInt(value.substring(yearSlashPos, timeColonPos));
        timeColonPos++;
        
        int timeDotPos = value.indexOf(".", yearSlashPos);
        hour = Integer.parseInt(value.substring(timeColonPos, timeDotPos));
        timeDotPos++;

        minute = Integer.parseInt(value.substring(timeDotPos, value.length()));
        
        return LocalDateTime.of(year, Month.of(month), day, hour, minute);
    }
    
    public static boolean openWebpage(URI uri) {
    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
            desktop.browse(uri);
            return true;
        } catch (Exception e) {
            LogJournal.error(e);
        }
    }
    return false;
}

public static boolean openWebpage(URL url) {
    try {
        return openWebpage(url.toURI());
    } catch (URISyntaxException e) {
            LogJournal.error(e);
    }
    return false;
}
}