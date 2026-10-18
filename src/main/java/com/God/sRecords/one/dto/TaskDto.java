package com.God.sRecords.one.dto;

import com.God.sRecords.one.model.Task;
import com.God.sRecords.one.model.TypeTask;
import lombok.Value;

import java.time.Instant;

/**
 * DTO for {@link com.God.sRecords.one.model.Task}
 */
@Value
public class TaskDto {
    Long id;
    String name;
    String body;
    TypeTask type;
    Instant updatedAt;
    Instant createdAt;


    public static TaskDto from(Task task) {
        return new TaskDto(task.getId(), task.getName(), task.getBody(), task.getType(), task.getUpdatedAt(), task.getCreatedAt());
    }
}

