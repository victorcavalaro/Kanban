package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.model.TaskStatus.DONE;
import static com.example.demo.model.TaskStatus.IN_PROGRESS;
import static com.example.demo.model.TaskStatus.TO_DO;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        task.setStatus(TO_DO);
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id)
                .map(existingTask -> updateExistingTask(existingTask, updatedTask))
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    private Task updateExistingTask(Task existingTask, Task updatedTask) {
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setPriority(updatedTask.getPriority());
        return taskRepository.save(existingTask);
    }

    public Task moveTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return moveTaskStatus(task);
    }

    private Task moveTaskStatus(Task task) {
        switch (task.getStatus()) {
            case TO_DO -> task.setStatus(IN_PROGRESS);
            case IN_PROGRESS -> task.setStatus(DONE);
            case DONE -> throw new IllegalStateException("Task already completed");
        }
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
