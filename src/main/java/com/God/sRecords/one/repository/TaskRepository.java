package com.God.sRecords.one.repository;

import com.God.sRecords.one.model.Task;
import com.God.sRecords.one.model.TypeTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdAndType(Long userId, TypeTask type);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByUserIdAndType(Long userId, TypeTask type);
}
