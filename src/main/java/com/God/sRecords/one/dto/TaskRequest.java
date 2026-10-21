package com.God.sRecords.one.dto;

import com.God.sRecords.one.model.TypeTask;

public class TaskRequest {
    private String name;
    private String body;
    private TypeTask type; // для обновления

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public TypeTask getType() {
        return type;
    }

    public void setType(TypeTask type) {
        this.type = type;
    }

    // геттеры и сеттеры обязательны
}
