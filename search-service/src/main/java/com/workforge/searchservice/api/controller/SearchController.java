package com.workforge.searchservice.api.controller;

import com.workforge.searchservice.application.service.SearchService;
import com.workforge.searchservice.domain.document.JobOfferDocument;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@AllArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<JobOfferDocument>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(searchService.search(keyword));
    }
}
