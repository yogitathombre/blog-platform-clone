package com.example.clonemedium.controller;

import com.example.clonemedium.model.Topic;
import com.example.clonemedium.model.User;
import com.example.clonemedium.service.TopicService;
import com.example.clonemedium.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;
    private final UserRepository userRepository;

    @Autowired
    public TopicController(TopicService topicService, UserRepository userRepository) {
        this.topicService = topicService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        List<Topic> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    @PostMapping("/create")
    public ResponseEntity<Topic> createTopic(@RequestBody String name) {
        Topic topic = topicService.createTopic(name);
        return ResponseEntity.ok(topic);
    }

    @PostMapping("/{topicId}/follow")
    public ResponseEntity<User> followTopic(@PathVariable Long topicId, @AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        String userEmail = oauth2User.getAttribute("email");
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).build(); // User not found
        }

        User user = userOptional.get();
        User updatedUser = topicService.followTopic(user.getId(), topicId);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/{topicId}/unfollow")
    public ResponseEntity<User> unfollowTopic(@PathVariable Long topicId, @AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        String userEmail = oauth2User.getAttribute("email");
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).build(); // User not found
        }

        User user = userOptional.get();
        User updatedUser = topicService.unfollowTopic(user.getId(), topicId);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/followed")
    public ResponseEntity<Set<Topic>> getFollowedTopics(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        String userEmail = oauth2User.getAttribute("email");
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).build(); // User not found
        }

        User user = userOptional.get();
        Set<Topic> followedTopics = topicService.getFollowedTopics(user.getId());
        return ResponseEntity.ok(followedTopics);
    }
} 