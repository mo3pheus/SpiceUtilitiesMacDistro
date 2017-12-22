package space.exploration.spice.utilities;

public class PositionUtilsTest {
    public static void main(String[] args) {

        String        utcTime       = "09/08/2016~14:32:32";
        PositionUtils positionUtils = new PositionUtils();
        positionUtils.setUtcTime(utcTime);
        for (String s : positionUtils.getPositionData()) {
            System.out.println(s);
        }

        System.out.println("PositionDataVectorLength = " + positionUtils.getPositionData().length);

        System.out.println("MSLRelPositionsPacket :: " + positionUtils.getPositionPacket());
    }
}
