package com.example.clonemedium.service;

import com.example.clonemedium.model.Topic;
import com.example.clonemedium.model.User;
import com.example.clonemedium.repository.TopicRepository;
import com.example.clonemedium.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;



@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository, UserRepository userRepository){ 
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Optional<Topic> getTopicByName(String name) {
        return topicRepository.findByName(name);
    }

    @Transactional
    public User followTopic(Long userId, Long topicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        user.getFollowedTopics().add(topic);
        topic.getFollowers().add(user);
        return userRepository.save(user);
    }

    @Transactional
    public User unfollowTopic(Long userId, Long topicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        user.getFollowedTopics().remove(topic);
        topic.getFollowers().remove(user);
        return userRepository.save(user);
    }

    public Set<Topic> getFollowedTopics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getFollowedTopics();
    }

    public Topic createTopic(String name) {
        if (topicRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Topic with name " + name + " already exists");
        }
        Topic newTopic = new Topic(name);
        return topicRepository.save(newTopic);
    }
}
 

