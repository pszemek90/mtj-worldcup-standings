package com.pszemek.mtjworldcupstandings.repository;

import com.pszemek.mtjworldcupstandings.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByMatchId(Integer matchId);
}
