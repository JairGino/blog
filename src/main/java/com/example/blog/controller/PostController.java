package com.example.blog.controller;

import com.example.blog.model.Post;
import com.example.blog.repository.PostRepository;
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
@RequestMapping("/posts")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TopicRepository topicRepository;

    @GetMapping
    public ResponseEntity<List<Post>> getAll() {
        return ResponseEntity.ok(postRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(response -> ResponseEntity.ok(response))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Post>> getByTitle(@PathVariable String title) {
        return ResponseEntity.ok(postRepository.findAllByTitleContainingIgnoreCase(title));
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post) {
        if (topicRepository.existsById(post.getTopic().getId()))
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(postRepository.save(post));

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic não existe!", null);
    }

    @PutMapping
    public ResponseEntity<Post> updatePost(@Valid @RequestBody Post post) {
        if (postRepository.existsById(post.getId())) {

            if (topicRepository.existsById(post.getTopic().getId()))
                return postRepository.findById(post.getId())
                        .map(response -> ResponseEntity.status(HttpStatus.OK)
                                .body(postRepository.save(post)))
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic não existe!", null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        Optional<Post> post = postRepository.findById(id);

        if (post.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        postRepository.deleteById(id);
    }
}
