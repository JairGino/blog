package com.example.blog.controller;

import com.example.blog.model.Topic;
import com.example.blog.repository.TopicRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topics")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;

    @GetMapping
    public ResponseEntity<List<Topic>> getAll() {
        return ResponseEntity.ok(topicRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Topic> getById(@PathVariable Long id) {
        return topicRepository.findById(id)
                .map(response -> ResponseEntity.ok(response))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/topic/{topicName}")
    public ResponseEntity<List<Topic>> getByTopicName(@PathVariable String topicName) {
        return ResponseEntity.ok(topicRepository
                .findAllByNameContainingIgnoreCase(topicName));
    }

    @PostMapping()
    public ResponseEntity<Topic> createTopic(@Valid @RequestBody Topic topic) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(topicRepository.save(topic));
    }

    @PutMapping
    public ResponseEntity<Topic> updateTopic(@Valid @RequestBody Topic topic) {
        return topicRepository.findById(topic.getId())
                .map(response -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(topicRepository.save(topic)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Optional<Topic> topic = topicRepository.findById(id);

        if (topic.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        topicRepository.deleteById(id);
    }
}
