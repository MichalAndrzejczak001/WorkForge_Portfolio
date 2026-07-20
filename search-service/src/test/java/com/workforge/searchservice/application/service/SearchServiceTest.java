package com.workforge.searchservice.application.service;

import com.workforge.searchservice.domain.document.JobOfferDocument;
import com.workforge.searchservice.infrastructure.persistence.JobSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock
    private JobSearchRepository jobSearchRepository;

    @InjectMocks
    private SearchService searchService;

    @Test
    void search_shouldReturnMatchingJobOffers_whenRepositoryReturnsResult() {
        // GIVEN
        String keyword = "Java";
        JobOfferDocument document = JobOfferDocument.builder()
                .title("Java Developer")
                .description("We are looking for Java Developer with Spring Boot experience.")
                .build();
        List<JobOfferDocument> expectedResult = List.of(document);
        when(jobSearchRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword))
                .thenReturn(expectedResult);

        // WHEN
        List<JobOfferDocument> actualResult = searchService.search(keyword);

        // THEN
        assertThat(actualResult).hasSize(1);
        assertThat(actualResult.get(0).getTitle()).isEqualTo("Java Developer");
    }

    @Test
    void search_shouldReturnEmptyList_whenNoResultsFound() {
        // GIVEN
        String keyword = "Rust";
        when(jobSearchRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword))
                .thenReturn(List.of());

        // WHEN
        List<JobOfferDocument> actualResult = searchService.search(keyword);

        // THEN
        assertThat(actualResult).isEmpty();
    }
}
