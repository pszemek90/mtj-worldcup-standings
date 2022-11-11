package com.pszemek.mtjworldcupstandings.repository;

import com.pszemek.mtjworldcupstandings.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {
}
