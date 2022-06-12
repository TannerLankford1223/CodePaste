package com.example.codepaste.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "snippets")
public class CodeSnippet {

//    @JsonIgnore
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column
    private String code;

    @CreationTimestamp
    @Column
    private LocalDateTime date;

    @Column
    private int timeRemaining;

    @Column
    private int views;

    private boolean viewRestriction = true;

    private boolean timeRestriction = true;

    private boolean enabled = true;

    public CodeSnippet(String code, int timeRemaining, int views) {
        this.code = code;
        this.timeRemaining = timeRemaining;
        this.views = views;

        viewRestriction = views != 0;
        timeRestriction = timeRemaining != 0;
    }

    public CodeSnippet() {
    }

    public String getCode() {
        return code;
    }

    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return date.format(formatter);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isViewRestriction() {
        return viewRestriction;
    }

    public boolean isTimeRestriction() {
        return timeRestriction;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        if (timeRemaining <= 0) {
            timeRestriction = false;
            timeRemaining = 0;
        }
        this.timeRemaining = timeRemaining;
    }

    public void updateTime() {
        if (timeRestriction && enabled) {
            long timePassed = Duration.between(date.toLocalTime(), LocalTime.now()).toSeconds();
            this.timeRemaining -= timePassed;
            if (timeRemaining < 0) {
                enabled = false;
                timeRemaining = 0;
            }
        }
    }

    public long getViews() {
        return views;
    }

    public void setViews(int views) {
        if (views <= 0) {
            viewRestriction = false;
            views = 0;
        }
        this.views = views;
    }

    public void decrementViews() {
        if (viewRestriction) {
            views--;
            if (views <= 0) {
                enabled = false;
                views = 0;
            }
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "CodeSnippet{" +
                "code='" + code + '\'' +
                ", time=" + timeRemaining +
                ", views=" + views +
                '}';
    }
}

