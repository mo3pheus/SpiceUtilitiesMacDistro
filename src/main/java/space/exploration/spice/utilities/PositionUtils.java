package space.exploration.spice.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.exploration.communications.protocol.spice.MSLRelativePositions;

import java.io.*;

public class PositionUtils {
    private static final double ALIGNMENT_THRESHOLD = 1.0d;
    private              File   positionsCalcFile   = ExecUtils.getExecutionFile("/POSITIONS/finalPositionCalc");
    private              String utcTime             = "";
    private              Logger logger              = LoggerFactory.getLogger(PositionUtils.class);

    public PositionUtils() {

    }

    public void setUtcTime(String utcTime) {
        this.utcTime = utcTime;
        logger.debug(utcTime);
    }

    public String[] getPositionData() {
        return ExecUtils.getExecutionOutput(positionsCalcFile, utcTime);
    }

    public MSLRelativePositions.MSLRelPositionsPacket getPositionPacket() {
        /*et, stC0, stC1, stC2, stC3, stC4, stC5, ltCE, stE0, stE1, stE2, stE3, stE4, stE5, ltEC, posEC0, posEC1,
        posEC2, ltEC, angularSeparation*/
        MSLRelativePositions.MSLRelPositionsPacket.Builder mBuilder = MSLRelativePositions.MSLRelPositionsPacket
                .newBuilder();

        String[] positionsData = ExecUtils.getExecutionOutput(positionsCalcFile, utcTime);

        // Ephemeris Time
        mBuilder.setEphemerisTime(Double.parseDouble(positionsData[0]));

        // x,y,z,vx,vy,vz
        mBuilder.addStateCuriosity(Double.parseDouble(positionsData[1]));
        mBuilder.addStateCuriosity(Double.parseDouble(positionsData[2]));
        mBuilder.addStateCuriosity(Double.parseDouble(positionsData[3]));
        mBuilder.addStateCuriosity(Double.parseDouble(positionsData[4]));
        mBuilder.addStateCuriosity(Double.parseDouble(positionsData[5]));
        mBuilder.addStateCuriosity(Double.parseDouble(positionsData[6]));
        mBuilder.setOwltMSLEarth(Double.parseDouble(positionsData[7]));

        // x,y,z,vx,vy,vz
        mBuilder.addStateEarth(Double.parseDouble(positionsData[8]));
        mBuilder.addStateEarth(Double.parseDouble(positionsData[9]));
        mBuilder.addStateEarth(Double.parseDouble(positionsData[10]));
        mBuilder.addStateEarth(Double.parseDouble(positionsData[11]));
        mBuilder.addStateEarth(Double.parseDouble(positionsData[12]));
        mBuilder.addStateEarth(Double.parseDouble(positionsData[13]));
        mBuilder.setOwltEarthMSL(Double.parseDouble(positionsData[14]));

        // pX, pY, pZ
        mBuilder.addPositionEarthWRTCuriosity(Double.parseDouble(positionsData[15]));
        mBuilder.addPositionEarthWRTCuriosity(Double.parseDouble(positionsData[16]));
        mBuilder.addPositionEarthWRTCuriosity(Double.parseDouble(positionsData[17]));
        mBuilder.setOwltEarthMSL2(Double.parseDouble(positionsData[18]));

        //Angular separation Earth vs MSL HGA
        mBuilder.setAngSepHGAEarth(Double.parseDouble(positionsData[19]));

        //set HGA Pass boolean
        mBuilder.setHgaPass(Math.abs(Double.parseDouble(positionsData[19])) < ALIGNMENT_THRESHOLD);
        mBuilder.setSclkValue(positionsData[20]);
        mBuilder.setSol(getSol(positionsData[20]));

        //utcTime
        mBuilder.setUtcTime(utcTime);

        return mBuilder.build();
    }

    private int getSol(String sclkTime) {
        String solPart = sclkTime.split("/")[1];
        return Integer.parseInt(solPart.split(":")[0]);
    }
}
