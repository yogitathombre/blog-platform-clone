package com.example.clonemedium.repository;

import com.example.clonemedium.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findByName(String name);
} 