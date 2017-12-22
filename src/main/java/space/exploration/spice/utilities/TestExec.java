package space.exploration.spice.utilities;

public class TestExec {

    public static void main(String[] args) throws Exception {
        String    inputTime = "09-30-2016~15:32:32";
        TimeUtils timeUtils = new TimeUtils();
        timeUtils.updateClock(inputTime);

        System.out.println("CalendarTime is :: " + timeUtils.getCalendarTime());
        System.out.println("Clock File is :: " + timeUtils.getClockFile());
        System.out.println("Ephemeris Time is :: " + timeUtils.getEphemerisTime());
        System.out.println("SclkTime is :: " + timeUtils.getSclkTime());
        System.out.println("UTCTime is :: " + timeUtils.getUtcTime());
        System.out.println("Sol is ::" + timeUtils.getSol());
        System.out.println("Applicable timeFrame is :: " + timeUtils.getApplicableTimeFrame());
    }
}
