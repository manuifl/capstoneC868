package org.manuel.utilities.formatters;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TZConvert {

    private static final ZoneId estZoneID = ZoneId.of("America/New_York");

    /**
     * Used for database writing, converts LocalDateTime at system default ZoneId with the same
     * instant at UTC
     * @param ldt LocalDateTime
     * @return Timestamp
     */
    public static Timestamp convertAtLocalToUTC(LocalDateTime ldt) {
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        return Timestamp.valueOf(zdt.toLocalDateTime());
    }
    /**
     * Used for database reading, converts Timestamp in UTC to LocalDateTime
     * @param tsIn Timestamp
     * @return Timestamp
     */
    public static Timestamp convertAtUTCToLocal(Timestamp tsIn) {
        LocalDateTime ldt = tsIn.toLocalDateTime();
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());
        return Timestamp.valueOf(zdt.toLocalDateTime());
    }

    public static ZonedDateTime getEquivalentToEST(LocalDateTime ldt){
        return ldt.atZone(estZoneID).withZoneSameInstant(ZoneId.systemDefault());
    }


    public ZoneId getEstZoneID() {
        return estZoneID;
    }

}
