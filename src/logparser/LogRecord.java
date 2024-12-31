package logparser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LogRecord {

    final String ip;
    final String user;
    final Date time;
    final Event event;
    Integer taskNumber;
    final Status status;

    public Integer getTaskNumber() {
        return taskNumber;
    }

    public String getIp() {
        return ip;
    }

    public String getUser() {
        return user;
    }

    public Date getTime() {
        return time;
    }

    public Event getEvent() {
        return event;
    }

    public Status getStatus() {
        return status;
    }

    public LogRecord(String[] properties) throws ParseException{
        ip = properties[0];
        user = properties[1];
        time = getDateFromString(properties[2]);
        String[] eventTaskNo = properties[3].split("\s");
        event = Event.valueOf(eventTaskNo[0]);
        taskNumber = eventTaskNo.length==2 ? Integer.parseInt(eventTaskNo[1]) : null;
        status = Status.valueOf(properties[4]);
    }

    private static Date getDateFromString(String str) throws ParseException {
        DateFormat format = new SimpleDateFormat("d.MM.y h:m:s");
        return format.parse(str);
    }

    public Map<String, Object> toMap(){
        return Map.of(
                "ip", ip,
                "user", user,
                "time", time,
                "event", event,
                "status", status
        );
    }

}