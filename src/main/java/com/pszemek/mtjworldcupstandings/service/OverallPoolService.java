package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.entity.OverallPool;
import com.pszemek.mtjworldcupstandings.repository.OverallPoolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OverallPoolService {

    private static final Logger logger = LoggerFactory.getLogger(OverallPoolService.class);

    private final OverallPoolRepository overallPoolRepository;

    public OverallPoolService(OverallPoolRepository overallPoolRepository) {
        this.overallPoolRepository = overallPoolRepository;
    }

    public OverallPool getOverallPool() {
        List<OverallPool> overallPool = overallPoolRepository.findAll();
        if(overallPool.size() != 1) {
            logger.error("Found more than one overall pool in database! Actual found pools: {}", overallPool.size());
            throw new IllegalStateException("Found more than one overall pool in database!");
        }
        return overallPool.get(0);
    }

    public void riseOverallPool(BigDecimal pool) {
        logger.info("Rising overall pool by: {}", pool);
        OverallPool overallPool = getOverallPool();
        overallPool.setAmount(overallPool.getAmount().add(pool));
        overallPoolRepository.save(overallPool);
    }
}
