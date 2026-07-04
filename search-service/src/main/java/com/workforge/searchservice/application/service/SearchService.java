package com.workforge.searchservice.application.service;

import com.workforge.searchservice.domain.document.JobOfferDocument;
import com.workforge.searchservice.infrastructure.persistence.JobSearchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SearchService {
    private final JobSearchRepository jobSearchRepository;

    public List<JobOfferDocument> search(String keyword) {
        return jobSearchRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword);
    }

}
