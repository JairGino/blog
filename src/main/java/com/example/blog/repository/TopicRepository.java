package com.example.blog.repository;

import com.example.blog.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    public List<Topic> findAllByNameContainingIgnoreCase(@Param("description") String description);

}
