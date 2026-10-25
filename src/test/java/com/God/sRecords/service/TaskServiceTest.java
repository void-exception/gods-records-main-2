package com.God.sRecords.service;

import com.God.sRecords.one.model.Task;
import com.God.sRecords.one.model.TypeTask;
import com.God.sRecords.one.regestration.model.User;
import com.God.sRecords.one.repository.TaskRepository;
import com.God.sRecords.one.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User owner;
    private User stranger;
    private Task task;

    @BeforeEach
    void setUp() {
        owner = new User("Иван", "ivan@mail.com", "x");
        owner.setId(1L);

        stranger = new User("Пётр", "petr@mail.com", "x");
        stranger.setId(2L);

        task = new Task("Заметка", "Текст", owner);
        task.setId(10L);
    }

    @Test
    void createTask_savesNormalTaskForUser() {
        taskService.createTask("Заголовок", "Тело", owner);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        Task saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Заголовок");
        assertThat(saved.getBody()).isEqualTo("Тело");
        assertThat(saved.getType()).isEqualTo(TypeTask.NORMAL);
        assertThat(saved.getUser()).isSameAs(owner);
    }

    @Test
    void updateTask_updatesFields_whenUserIsOwner() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        taskService.updateTask(10L, "Новое", "Новый текст", TypeTask.ARCHIVED, owner);

        assertThat(task.getName()).isEqualTo("Новое");
        assertThat(task.getBody()).isEqualTo("Новый текст");
        assertThat(task.getType()).isEqualTo(TypeTask.ARCHIVED);
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_doesNothing_whenUserIsNotOwner() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        taskService.updateTask(10L, "Взлом", "Взлом", TypeTask.TRASH, stranger);

        assertThat(task.getName()).isEqualTo("Заметка");
        assertThat(task.getType()).isEqualTo(TypeTask.NORMAL);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_throws_whenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(99L, "a", "b", TypeTask.NORMAL, owner))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void currentTask_returnsTask_onlyForOwner() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThat(taskService.currentTask(10L, owner)).contains(task);
        assertThat(taskService.currentTask(10L, stranger)).isEmpty();
    }

    @Test
    void tasksType_filtersByUserAndType() {
        when(taskRepository.findByUserIdAndType(1L, TypeTask.NORMAL)).thenReturn(List.of(task));

        List<Task> result = taskService.tasksType(owner, TypeTask.NORMAL);

        assertThat(result).containsExactly(task);
    }

    @Test
    void deleteTask_deletesOnlyTrashOfCurrentUser() {
        taskService.deleteTask(owner);

        verify(taskRepository).deleteByUserIdAndType(1L, TypeTask.TRASH);
    }
}