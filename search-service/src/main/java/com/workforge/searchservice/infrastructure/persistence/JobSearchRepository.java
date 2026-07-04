package com.workforge.searchservice.infrastructure.persistence;


import com.workforge.searchservice.domain.document.JobOfferDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface JobSearchRepository extends ElasticsearchRepository<JobOfferDocument, String> {
    List<JobOfferDocument> findByTitleContainingOrDescriptionContaining(String title, String description);
}
