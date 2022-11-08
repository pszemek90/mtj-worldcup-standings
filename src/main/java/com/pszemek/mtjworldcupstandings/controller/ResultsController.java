package com.pszemek.mtjworldcupstandings.controller;

import com.pszemek.mtjworldcupstandings.dto.CorrectTypingOutput;
import com.pszemek.mtjworldcupstandings.service.ResultsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/results")
public class ResultsController {

    private final ResultsService resultsService;

    public ResultsController(ResultsService resultsService) {
        this.resultsService = resultsService;
    }

    @GetMapping()
    public Map<String, List<CorrectTypingOutput>> getAllResults() {
        return resultsService.getAllResults();
    }

}
