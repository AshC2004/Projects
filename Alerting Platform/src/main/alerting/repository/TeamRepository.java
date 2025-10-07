package com.example.alerting.repository;

import com.example.alerting.model.Team;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TeamRepository {
    private final Map<String, Team> teams = new ConcurrentHashMap<>();

    public void create(Team team) { teams.put(team.getTeamId(), team); }

    public Team get(String teamId) { return teams.get(teamId); }

    public List<Team> listAll() { return new ArrayList<>(teams.values()); }
}
