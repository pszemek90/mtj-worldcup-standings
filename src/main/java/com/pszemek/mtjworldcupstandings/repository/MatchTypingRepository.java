package com.pszemek.mtjworldcupstandings.repository;

import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchTypingRepository extends JpaRepository<MatchTyping, Long> {
    Optional<MatchTyping> findByUserIdAndMatchId(Long userId, Integer matchId);
}
