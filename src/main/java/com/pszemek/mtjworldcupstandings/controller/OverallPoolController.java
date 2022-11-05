package com.pszemek.mtjworldcupstandings.controller;

import com.pszemek.mtjworldcupstandings.dto.OverallPoolDto;
import com.pszemek.mtjworldcupstandings.mapper.OverallPoolMapper;
import com.pszemek.mtjworldcupstandings.service.OverallPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/overallPool")
public class OverallPoolController {

    private static final Logger logger = LoggerFactory.getLogger(OverallPoolController.class);

    private final OverallPoolService overallPoolService;

    public OverallPoolController(OverallPoolService overallPoolService) {
        this.overallPoolService = overallPoolService;
    }

    @GetMapping()
    public OverallPoolDto getOverallPool() {
        logger.info("Fetching overall pool");
        return OverallPoolMapper.mapFromEntity(overallPoolService.getOverallPool());
    }
}
