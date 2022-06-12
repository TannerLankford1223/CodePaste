package com.example.codepaste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ResponseDTO {

    @JsonProperty("id")
    private UUID id;
    @JsonProperty("code")
    private String code;

    public ResponseDTO(UUID id, String code) {
        this.id = id;
        this.code = code;
    }

    public ResponseDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
