package logparser;

import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Solution {

    public static void main(String[] args) throws ParseException {
        LogParser logParser = new LogParser(Paths.get("C:\\Users\\maksimzelinskyi\\IdeaProjects\\javarush-projects\\src\\logparser\\logs"));
        //System.out.println(logParser.getDatesWhenErrorHappened(null, null));
        //System.out.println(logParser.getDateWhenUserDoneTask("Vasya Pupkin", 15, null, null));
        System.out.println(logParser.getNumberOfAttemptToSolveTask(18, null, null));
        System.out.println(logParser.getAllEvents(new SimpleDateFormat("y.M.d").parse("2018.4.30"), new Date()));
        System.out.println(logParser.execute("get user for ip = \"192.168.100.2\""));
    }
}