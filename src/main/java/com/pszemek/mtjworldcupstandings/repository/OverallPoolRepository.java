package com.pszemek.mtjworldcupstandings.repository;

import com.pszemek.mtjworldcupstandings.entity.OverallPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OverallPoolRepository extends JpaRepository<OverallPool, Long> {
}
