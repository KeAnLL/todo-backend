package com.example.todobackend.controller;

import com.example.todobackend.domain.Todo;
import com.example.todobackend.exception.ResourceNotFoundException;
import com.example.todobackend.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TodoController {
    @Autowired
    private TodoRepository todoRepository;

    /**
     * Retrieve all the to-do items from the database
     * @return list of to-do items
     */
    @GetMapping("/todos")
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    /**
     * Retrieve the specific to-do item using the provided ID
     * @param todoId user-provided ID
     * @return response body that indicates OK
     * @throws ResourceNotFoundException user-requested to-do item is not available in the database
     */
    @GetMapping("/todo/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable(value = "id") Long todoId) throws ResourceNotFoundException {
        Optional<Todo> maybeTodo = todoRepository.findById(todoId);

        if (maybeTodo.isEmpty()) {
            throw new ResourceNotFoundException("Fail to retrieve todo item");
        } else {
            return ResponseEntity.ok().body(maybeTodo.get());
        }
    }

    /**
     * Add a new to-do item to the database
     * @param todo the to-do instance
     * @return the created to-do item
     */
    @PostMapping("/todo")
    public Todo createTodo(@RequestBody Todo todo) {
        return todoRepository.save(todo);
    }

    @PutMapping("/todo/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable(value = "id") Long todoId, @RequestBody Todo todoDetails) throws ResourceNotFoundException {
        Optional<Todo> maybeTodo = todoRepository.findById(todoId);

        if (maybeTodo.isEmpty()) {
            throw new ResourceNotFoundException("Fail to update todo item");
        } else {
            Todo todo = maybeTodo.get();
            todo.setTitle(todoDetails.getTitle());
            todo.setDescription(todoDetails.getDescription());
            todo.setCompleted(todoDetails.isCompleted());

            final Todo updatedTodo = todoRepository.save(todo);

            return ResponseEntity.ok(updatedTodo);
        }
    }

    @DeleteMapping("/todo/{id}")
    public Map<String, Boolean> deleteTodo(@PathVariable(value = "id") Long todoId) throws ResourceNotFoundException {
        Optional<Todo> maybeTodo = todoRepository.findById(todoId);

        if (maybeTodo.isEmpty()) {
            throw new ResourceNotFoundException("Fail to delete todo item");
        } else {
            todoRepository.delete(maybeTodo.get());
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return response;
        }
    }
}
