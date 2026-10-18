package com.God.sRecords.one.controller;

import com.God.sRecords.one.dto.TaskDto;
import com.God.sRecords.one.model.Task;
import com.God.sRecords.one.model.TypeTask;
import com.God.sRecords.one.regestration.model.User;
import com.God.sRecords.one.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<Void> createTask(
                                        @AuthenticationPrincipal User user,
                                        @RequestParam("name") String name,
                                        @RequestParam("body") String body
                                        ){
        taskService.createTask(name, body, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateTask(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("body") String body,
            @RequestParam("type") TypeTask type
            ){
        taskService.updateTask(id, name, body, type, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskDto>> getAllTasks(@AuthenticationPrincipal User user){
        List<TaskDto> tasks = taskService.allTask(user).stream()
                .map(TaskDto::from)
                .toList();
        return ResponseEntity.ok().body(tasks);
    }

    @DeleteMapping("delete")
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal User user){
        taskService.deleteTask(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("task/{id}")
    public ResponseEntity<?> getTask(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ){
                Task task = taskService.currentTask(id, user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok().body(TaskDto.from(task));
    }

    @GetMapping("/all/type")
    public ResponseEntity<List<TaskDto>> getAllTasksType(
            @AuthenticationPrincipal User user,
            @RequestParam("type") TypeTask type
            ){

        List<TaskDto> tasks = taskService.tasksType(user, type).stream()
                .map(TaskDto::from)
                .toList();
        return ResponseEntity.ok().body(tasks);
    }

}
