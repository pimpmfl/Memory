package at.qe.skeleton.model;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Log implements Comparable<Log>, Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogId", nullable = false)
    private long logId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name= "logEvent", nullable = false)
    private LogEvent logEvent;

    public long getLogId() {
        return logId;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setLogEvent(LogEvent logEvent) {
        this.logEvent = logEvent;
    }

    @Override
    public int compareTo(Log o) {
        return Long.compare(this.getLogId(), o.logId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return logId == log.logId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId);
    }
}
