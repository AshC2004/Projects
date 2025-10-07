package com.example.alerting.repository;

import com.example.alerting.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public void create(User user) {
        users.put(user.getUserId(), user);
    }

    public User get(String userId) { return users.get(userId); }

    public List<User> listAll() { return new ArrayList<>(users.values()); }

    public List<User> getUsersByIds(List<String> ids) {
        return ids.stream().map(users::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<User> getUsersByTeamIds(List<String> teamIds) {
        return users.values().stream()
                .filter(u -> u.getTeams().stream().anyMatch(teamIds::contains))
                .collect(Collectors.toList());
    }
}
