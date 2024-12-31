package logparser;

import logparser.query.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LogParser implements IPQuery, UserQuery, DateQuery, EventQuery, QLQuery {

    private final Path logDir;

    public LogParser(Path logDir) {
        this.logDir = logDir;
    }

    private List<LogRecord> getLogRecords(Date after, Date before) {
        List<LogRecord> list = new ArrayList<>();
        File dirFile = logDir.toFile();
        File[] logFiles = dirFile.listFiles((dir, name) -> name.endsWith(".log"));
        for (File logFile : logFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        LogRecord logRecord = new LogRecord(line.split("\t"));
                        if ((before == null || logRecord.getTime().before(before)) && (after == null || logRecord.getTime().after(after))) {
                            list.add(logRecord);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {

            }
        }
        return list;
    }

    private <R> Set<R> getPropertySetWithCondition(Function<LogRecord, R> mapFunction, Predicate<LogRecord> condition, Date after, Date before) {
        return getLogRecords(after, before).stream()
                .filter(condition != null ? condition : x -> true)
                .map(mapFunction)
                .collect(Collectors.toSet());
    }

    private long countMatches(Predicate<LogRecord> condition, Date after, Date before){
       return getLogRecords(after, before).stream().filter(condition).count();
    }

    private Set<String> getIPsWithCondition(Predicate<LogRecord> condition, Date after, Date before) {
        return getPropertySetWithCondition(LogRecord::getIp, condition, after, before);
    }

    private Set<String> getUsersWithCondition(Predicate<LogRecord> condition, Date after, Date before) {
        return getPropertySetWithCondition(LogRecord::getUser, condition, after, before);
    }

    private Set<Date> getDatesWithCondition(Predicate<LogRecord> condition, Date after, Date before) {
        return getPropertySetWithCondition(LogRecord::getTime, condition, after, before);
    }

    private Set<Event> getEventsWithCondition(Predicate<LogRecord> condition, Date after, Date before) {
        return getPropertySetWithCondition(LogRecord::getEvent, condition, after, before);
    }

    @Override
    public int getNumberOfUniqueIPs(Date after, Date before) {
        return getUniqueIPs(after, before).size();
    }

    @Override
    public Set<String> getUniqueIPs(Date after, Date before) {
        return getIPsWithCondition(null, after, before);
    }

    @Override
    public Set<String> getIPsForUser(String user, Date after, Date before) {
        return getIPsWithCondition(x -> x.getUser().equals(user), after, before);
    }

    @Override
    public Set<String> getIPsForEvent(Event event, Date after, Date before) {
        return getIPsWithCondition(x -> x.getEvent() == event, after, before);
    }

    @Override
    public Set<String> getIPsForStatus(Status status, Date after, Date before) {
        return getIPsWithCondition(x -> x.getStatus() == status, after, before);
    }

    @Override
    public Set<String> getAllUsers() {
        return getLoggedUsers(null, null);
    }

    @Override
    public int getNumberOfUsers(Date after, Date before) {
        return getUsersWithCondition(null, after, before).size();
    }

    @Override
    public int getNumberOfUserEvents(String user, Date after, Date before) {
        return getPropertySetWithCondition(LogRecord::getEvent, x -> x.getUser().equals(user), after, before).size();
    }

    @Override
    public Set<String> getUsersForIP(String ip, Date after, Date before) {
        return getUsersWithCondition(x -> x.getIp().equals(ip), after, before);
    }

    @Override
    public Set<String> getLoggedUsers(Date after, Date before) {
        return getUsersWithCondition(null, after, before);
    }

    @Override
    public Set<String> getDownloadedPluginUsers(Date after, Date before) {
        return getUsersWithCondition(x -> x.getEvent() == Event.DOWNLOAD_PLUGIN, after, before);
    }

    @Override
    public Set<String> getWroteMessageUsers(Date after, Date before) {
        return getUsersWithCondition(x -> x.getEvent() == Event.WRITE_MESSAGE, after, before);
    }

    @Override
    public Set<String> getSolvedTaskUsers(Date after, Date before) {
        return getUsersWithCondition(x -> x.getEvent() == Event.SOLVE_TASK, after, before);
    }

    @Override
    public Set<String> getSolvedTaskUsers(Date after, Date before, int task) {
        return getUsersWithCondition(x -> x.getEvent() == Event.SOLVE_TASK && Objects.equals(x.getTaskNumber(), task), after, before);
    }

    @Override
    public Set<String> getDoneTaskUsers(Date after, Date before) {
        return getUsersWithCondition(x -> x.getEvent() == Event.DONE_TASK, after, before);
    }

    @Override
    public Set<String> getDoneTaskUsers(Date after, Date before, int task) {
        return getUsersWithCondition(x -> (x.getEvent() == Event.DONE_TASK) && (Objects.equals(x.getTaskNumber(), task)), after, before);
    }

    @Override
    public Set<Date> getDatesForUserAndEvent(String user, Event event, Date after, Date before) {
        return getDatesWithCondition(x -> x.getUser().equals(user) && (x.getEvent() == event), after, before);
    }

    @Override
    public Set<Date> getDatesWhenSomethingFailed(Date after, Date before) {
        return getDatesWithCondition(x -> x.getStatus() == Status.FAILED, after, before);
    }

    @Override
    public Set<Date> getDatesWhenErrorHappened(Date after, Date before) {
        return getDatesWithCondition(x -> x.getStatus() == Status.ERROR, after, before);
    }

    @Override
    public Date getDateWhenUserLoggedFirstTime(String user, Date after, Date before) {
        return getDatesWithCondition(x -> x.getUser().equals(user), after, before).stream().min(Comparator.naturalOrder()).get();
    }

    @Override
    public Date getDateWhenUserSolvedTask(String user, int task, Date after, Date before) {
        return getDatesWithCondition(x -> x.getUser().equals(user)
                                            && x.getEvent() == Event.SOLVE_TASK
                                            && Objects.equals(x.getTaskNumber(), task), after, before)
                .stream().findFirst().orElse(null);
    }

    @Override
    public Date getDateWhenUserDoneTask(String user, int task, Date after, Date before) {
        return getDatesWithCondition(x -> x.getUser().equals(user)
                                  && x.getEvent() == Event.DONE_TASK
                                  && Objects.equals(x.getTaskNumber(), task)
                                  && x.getStatus()==Status.OK, after, before)
                .stream().findFirst().orElse(null);
    }

    @Override
    public Set<Date> getDatesWhenUserWroteMessage(String user, Date after, Date before) {
        return getDatesWithCondition(x -> x.getUser().equals(user) && x.getEvent() == Event.WRITE_MESSAGE, after, before);
    }

    @Override
    public Set<Date> getDatesWhenUserDownloadedPlugin(String user, Date after, Date before) {
        return getDatesWithCondition(x -> x.getUser().equals(user) && x.getEvent() == Event.DOWNLOAD_PLUGIN, after, before);
    }

    @Override
    public int getNumberOfAllEvents(Date after, Date before) {
        return getAllEvents(after, before).size();
    }

    @Override
    public Set<Event> getAllEvents(Date after, Date before) {
        return getEventsWithCondition(null, after, before);
    }

    @Override
    public Set<Event> getEventsForIP(String ip, Date after, Date before) {
        return getEventsWithCondition(x -> x.getIp().equals(ip), after, before);
    }

    @Override
    public Set<Event> getEventsForUser(String user, Date after, Date before) {
        return getEventsWithCondition(x->x.getUser().equals(user), after, before);
    }

    @Override
    public Set<Event> getFailedEvents(Date after, Date before) {
        return getEventsWithCondition(x->x.getStatus()==Status.FAILED, after, before);
    }

    @Override
    public Set<Event> getErrorEvents(Date after, Date before) {
        return getEventsWithCondition(x->x.getStatus()==Status.ERROR, after, before);
    }

    @Override
    public int getNumberOfAttemptToSolveTask(int task, Date after, Date before) {
        return (int) countMatches(x -> x.getEvent()==Event.SOLVE_TASK && Objects.equals(x.getTaskNumber(), task), after, before);
    }

    @Override
    public int getNumberOfSuccessfulAttemptToSolveTask(int task, Date after, Date before) {
        return (int) countMatches(x -> x.getEvent()==Event.DONE_TASK
                                    && Objects.equals(x.getTaskNumber(), task)
                                    && x.getStatus()==Status.OK, after, before);
    }

    @Override
    public Map<Integer, Integer> getAllSolvedTasksAndTheirNumber(Date after, Date before) {
        Map<Integer, Integer> map = new HashMap<>();
        Set<Integer> tasks = getPropertySetWithCondition(LogRecord::getTaskNumber, null, after, before);
        for(Integer task : tasks){
            map.put(task, getNumberOfAttemptToSolveTask(task, after, before));
        }
        return map;
    }

    @Override
    public Map<Integer, Integer> getAllDoneTasksAndTheirNumber(Date after, Date before) {
        Map<Integer, Integer> map = new HashMap<>();
        Set<Integer> tasks = getPropertySetWithCondition(LogRecord::getTaskNumber, null, after, before);
        for(Integer task : tasks){
            map.put(task, getNumberOfSuccessfulAttemptToSolveTask(task, after, before));
        }
        return map;
    }

    @Override
    public Set<Object> execute(String query) {
        String[] array = query.split("\s");
        if("get".equals(array[0])) {
            Predicate<LogRecord> condition = null;
            if(array.length>2 && array[2].equals("for")){
                condition = x->x.toMap().get(array[3]).equals(array[5].replaceAll("\"", ""));
            }
            switch (array[1]) {
                case "ip":
                    return new HashSet<>(getPropertySetWithCondition(LogRecord::getIp, condition, null, null));
                case "user":
                    return new HashSet<>(getPropertySetWithCondition(LogRecord::getUser, condition, null, null));
                case "time":
                    return new HashSet<>(getPropertySetWithCondition(LogRecord::getTime, condition, null, null));
                case "event":
                    return new HashSet<>(getPropertySetWithCondition(LogRecord::getEvent, condition, null, null));
                case "status":
                    return new HashSet<>(getPropertySetWithCondition(LogRecord::getStatus, condition, null, null));
            }
        }
        return Set.of();
    }

}