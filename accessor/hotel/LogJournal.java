package accessor.hotel;

import accessor.hotel.model.Journal;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogJournal {
    
    private static String journalName = "";
    private static FileWriter writer;
    
    public static void open() {
        try {
            String fileName = getCurrentDateTime();
            journalName = fileName;
            File logFile = new File(getLogLocation(), fileName + ".log");
            appendNextLog();
            writer = new FileWriter(logFile);
            note("Лог запушен!");
        } catch (IOException ex) {
                throw new RuntimeException(ex);
        }
    }
    
    private static void appendNextLog() throws IOException {
        try (FileWriter journalsListWriter = new FileWriter(new File(getLogLocation(), "journals.lst"), true)) {
            journalsListWriter.append(journalName).append("\n");
            journalsListWriter.flush();
        }
    }
    
    public static void close() {
        try {
            note("Лог остановлен!");
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void error(Exception e)  {
        StringBuilder builder = new StringBuilder(e.toString());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            builder.append("     \n").append(stackTraceElement.toString());
        }
        error(builder.toString());
        throw new RuntimeException(e);
    }
    
    public static void error(String msg)  {
        MainApp.showError(journalName);
        try {
            writer.append("\nError [" + getCurrentDateTime() + "]: " + msg);
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void note(String msg) {
        try {
            writer.append("\n[" + getCurrentDateTime() + "]: " + msg);
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static String getCurrentDateTime() {
        return Util.datetimeToName(LocalDateTime.now());
    }
    
    private static File getLogLocation() {
        File logPath = new File(Paths.LOG_PATH);
        logPath.mkdirs();
        return logPath;
    }
    
    public static List<Journal> loadJournals() {
        List<Journal> journals = new ArrayList<>();
        File[] listFiles = getLogLocation().listFiles((File pathname) -> pathname.isFile() && pathname.getName().endsWith(".log"));
        for (File file : listFiles) {
            try (FileReader redaer = new FileReader(file)) {
                char[] cbuf = new char[2048];
                StringBuilder builder = new StringBuilder();
                int len;
                while((len = redaer.read(cbuf)) != -1) {
                    builder.append(cbuf, 0, len);
                }
                Journal journal = new Journal(file, builder.toString());
                journals.add(journal);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return journals;
    }
}