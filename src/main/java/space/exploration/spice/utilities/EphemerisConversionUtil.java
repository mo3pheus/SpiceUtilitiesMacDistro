package space.exploration.spice.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class EphemerisConversionUtil {
    File clockFile = null;
    private double ephemerisTime = 0.0d;
    private String calendarTime  = "";
    private String sclkTime      = "";
    private String utcTime       = "";
    private Logger logger        = LoggerFactory.getLogger(TimeUtils.class);

    public enum SCHEMA {
        SCLK_STR(0), EPHEMERIS_TIME(1), CALENDAR_TIME(2);
        int value;

        SCHEMA(int val) {
            value = val;
        }

        public String getSchema() {
            return "sclkString,ephemerisTime,calendarTime";
        }
    }

    public EphemerisConversionUtil() {
        clockFile = ExecUtils.getExecutionFile("/SCLK/ephemerisUtil.out");
    }

    public void updateClock(String ephemerisTimeString) {
        logger.debug(ephemerisTimeString);

        String[] outputParts = ExecUtils.getExecutionOutput(clockFile, ephemerisTimeString);
        sclkTime = outputParts[TimeUtils.SCHEMA.SCLK_STR.value];
        ephemerisTime = Double.parseDouble(outputParts[TimeUtils.SCHEMA.EPHEMERIS_TIME.value]);
        calendarTime = outputParts[TimeUtils.SCHEMA.CALENDAR_TIME.value];
    }

    public File getClockFile() {
        return clockFile;
    }

    public double getEphemerisTime() {
        return ephemerisTime;
    }

    public String getCalendarTime() {
        return calendarTime;
    }

    public String getSclkTime() {
        return sclkTime;
    }

    public int getSol() {
        String solPart = sclkTime.split("/")[1];
        return Integer.parseInt(solPart.split(":")[0]);
    }

    public String getUtcTime() {
        return utcTime;
    }

    public String getApplicableTimeFrame() {
        return "APPLICABLE_START_TIME=2000-001T11:58:55.816, APPLICABLE_STOP_TIME=2017-360T19:17:40.149";
    }
}

