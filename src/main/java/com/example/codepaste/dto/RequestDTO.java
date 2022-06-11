package com.example.codepaste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestDTO {

    @JsonProperty("code")
    private String code;
    @JsonProperty("time")
    private int time;
    @JsonProperty("views")
    private  int views;

    public RequestDTO(String code, int time, int views) {
        this.code = code;
        this.time = time;
        this.views = views;
    }

    public RequestDTO() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\":\"" + code + '\"' +
                ", \"time\":" + time +
                ", \"views\":" + views +
                '}';
    }
}
