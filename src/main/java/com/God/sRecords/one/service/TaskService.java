package com.God.sRecords.one.service;

import com.God.sRecords.one.model.Task;
import com.God.sRecords.one.model.TypeTask;
import com.God.sRecords.one.regestration.model.User;
import com.God.sRecords.one.repository.TaskRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public void createTask(String name, String body, User user){
        Task task = new Task(name, body, user);
        taskRepository.save(task);
    }

    public void updateTask(Long id, String name, String body, TypeTask type, User user){
        Task task = taskRepository.findById(id).orElseThrow();
        if (task.getUser().getId().equals(user.getId())) {
            task.setName(name);
            task.setBody(body);
            task.setType(type);
            taskRepository.save(task);
        }
    }

    @Transactional
    public void deleteTask(User user){
        taskRepository.deleteByUserIdAndType(user.getId(), TypeTask.TRASH);
    }

    public List<Task> allTask(User user){
        return taskRepository.findByUserId(user.getId());
    }

    public Optional<Task> currentTask(Long id, User user){
        Optional<Task> task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()));;


        return task;
    }

    public List<Task> tasksType(User user, TypeTask type){
        return taskRepository.findByUserIdAndType(user.getId(), type);
    }
}
