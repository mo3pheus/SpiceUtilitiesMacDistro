package space.exploration.spice.dataUpload;

import java.sql.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import space.exploration.spice.utilities.TimeUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SclkDataUpload {

    public static final String            DATE_FORMAT    = "yyyy-MM-dd~HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT);

    private static Logger     logger      = LoggerFactory.getLogger(SclkDataUpload.class);
    private static Properties logDBConfig = null;

    static String dbUserName, dbPassword;
    static Statement  statement;
    static ResultSet  resultSet;
    static Connection logDBConnection;

    /**
     * @param args args[0] - startDate in given clock format.
     *             args[1] - number of months - missionDuration.
     *             args[2] - logging level
     *             args[3] - dbProperties file.
     */
    public static void main(String[] args) throws SQLException {
        configureLogging(Boolean.parseBoolean(args[2]));
        logger.info("\n Welcome to the data upload utility for sclk data");

        try {
            logDBConfig = convertToPropertyFiles(args[3]);
            configureDB(logDBConfig);

            DateTime  startDateTime  = DATE_FORMATTER.parseDateTime(args[0]).withZone(DateTimeZone.UTC);
            int       durationMonths = Integer.parseInt(args[1]);
            DateTime  endTime        = startDateTime.plusMonths(durationMonths);
            TimeUtils timeUtils      = new TimeUtils();

            while (startDateTime.isBefore(endTime)) {
                timeUtils.updateClock(DATE_FORMATTER.print(startDateTime));
                String sclkString = timeUtils.getSclkTime();
                int    sol        = timeUtils.getSol();
                String utcTime    = timeUtils.getUtcTime();
                writeSclkEntry(startDateTime.getMillis(), utcTime, sol, sclkString);
                startDateTime = startDateTime.plusSeconds(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            logDBConnection.close();
        }
    }

    public static void configureLogging(boolean debug) {
        FileAppender fa = new FileAppender();

        if (!debug) {
            fa.setThreshold(Level.toLevel(Priority.INFO_INT));
            fa.setFile("sclkDataUpload/sclkFile_" + Long.toString(System.currentTimeMillis()) + ".log");
        } else {
            fa.setThreshold(Level.toLevel(Priority.DEBUG_INT));
            fa.setFile("sclkDataUpload/analysisLogs/sclkFile_" + Long.toString(System.currentTimeMillis()) + ".log");
        }

        fa.setLayout(new PatternLayout("%d [%t] %p %c %x - %m%n"));

        fa.activateOptions();
        org.apache.log4j.Logger.getRootLogger().addAppender(fa);
    }

    public static Properties convertToPropertyFiles(String filePath) throws IOException {
        FileInputStream propFile = new FileInputStream(filePath);
        Properties      config   = new Properties();
        config.load(propFile);
        return config;
    }

    public static void configureDB(Properties logDBConfig) {

        try {
            System.out.println("Configuring database");
            dbUserName = logDBConfig.getProperty("mars.rover.database.user");
            dbPassword = logDBConfig.getProperty("mars.rover.database.password");
            logDBConnection = DriverManager
                    .getConnection("jdbc:mysql://" + logDBConfig.getProperty("mars.rover.database.host")
                                           + "/" + logDBConfig.getProperty("mars.rover.database.dbName")
                                           + "?user=" + dbUserName + "&password=" + dbPassword);
            statement = logDBConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet
                    .CONCUR_UPDATABLE);
            resultSet = statement.executeQuery("SELECT * FROM " + logDBConfig.getProperty("mars.rover.database" +
                                                                                                  ".sclkTableName"));
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public static void writeSclkEntry(long ephemerisTimeMs, String utcTime, int sol, String sclkString) {
        try {
            if (resultSet.isClosed()) {
                resultSet = statement.executeQuery("SELECT * FROM " + logDBConfig.getProperty("mars.rover.database" +
                                                                                                      ".logTableName"));
            }
            resultSet.moveToInsertRow();
            resultSet.updateDouble("ephemerisMs", (double) (ephemerisTimeMs));
            resultSet.updateString("sclkString", sclkString);
            resultSet.updateInt("sol", sol);
            resultSet.updateString("utcTime", utcTime);
            resultSet.insertRow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
