package space.exploration.spice.utilities;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class ExecUtils {

    private static final String EXECUTIONS_FILE = "executionsFile";

    public static File getExecutionFile(String filePath) {
        InputStream executionFileStream = TimeUtils.class.getResourceAsStream(filePath);
        File        executionsFile      = new File(EXECUTIONS_FILE + filePath.replace("/", "_"));
        executionsFile.setReadable(true);
        executionsFile.setExecutable(true);
        try {
            Thread.sleep(1000);
            OutputStream outputStream = new FileOutputStream(executionsFile);
            IOUtils.copy(executionFileStream, outputStream);
            outputStream.close();
        } catch (IOException io) {
            io.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return executionsFile;
    }

    public static String[] getExecutionOutput(File executionFile, String utcTime) {
        executionFile.setReadable(true);
        executionFile.setExecutable(true);
        Runtime runtime = Runtime.getRuntime();
        String  output  = "";
        try {
            String[]          commands = {"./" + executionFile.getPath(), utcTime};
            Process           process  = runtime.exec(commands);
            InputStream       is       = process.getInputStream();
            InputStreamReader isr      = new InputStreamReader(is);
            BufferedReader    br       = new BufferedReader(isr);
            output = br.readLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.split(",");
    }
}
