
package com.solari.supportintelligence.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solari.supportintelligence.dto.request.TicketSearchRequest;
import com.solari.supportintelligence.dto.response.TicketSearchResponse;
import com.solari.supportintelligence.dto.response.TicketSearchResult;
import com.solari.supportintelligence.repository.TicketSearchRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SupportTicketSearchService {

    private static final double MIN_RELEVANCE_SCORE = 0.03;

    private final TicketSearchRepository repository;

    public SupportTicketSearchService(TicketSearchRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public TicketSearchResponse search(TicketSearchRequest request) {
        if (request == null
                || blank(request.subject()) && blank(request.description())) {
            throw new IllegalArgumentException(
                    "Provide a ticket subject or description");
        }

        String originalQuery = ((request.subject() == null ? "" : request.subject())
                + " "
                + (request.description() == null ? "" : request.description()))
                .trim();

        String query = Arrays.stream(
                originalQuery.toLowerCase(Locale.ROOT)
                        .split("[^a-z0-9]+"))
                .filter(token -> token.length() >= 3)
                .distinct()
                .collect(Collectors.joining(" OR "));

        if (query.isBlank()) {
            return noMatches(originalQuery);
        }

        int limit = request.limit() == null
                ? 5
                : Math.max(1, Math.min(request.limit(), 20));

        List<TicketSearchResult> matches = repository.search(query, limit)
                .stream()
                .map(this::toResult)
                .filter(result -> result.relevanceScore() >= MIN_RELEVANCE_SCORE)
                .toList();

        if (matches.isEmpty()) {
            return noMatches(originalQuery);
        }

        return new TicketSearchResponse(
                originalQuery,
                "MATCHES_FOUND",
                "Found potentially relevant historical resolutions. Verify that the resolution applies to this ticket.",
                matches);
    }

    private TicketSearchResult toResult(Object[] row) {
        Object dateValue = row[6];

        LocalDate createdAt;
        if (dateValue instanceof LocalDate localDate) {
            createdAt = localDate;
        } else if (dateValue instanceof java.sql.Date sqlDate) {
            createdAt = sqlDate.toLocalDate();
        } else {
            throw new IllegalStateException(
                    "Unexpected created_at type: "
                            + (dateValue == null
                                    ? "null"
                                    : dateValue.getClass().getName()));
        }

        return new TicketSearchResult(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                (String) row[5],
                createdAt,
                (String) row[7],
                ((Number) row[8]).doubleValue());
    }

    private TicketSearchResponse noMatches(String query) {
        return new TicketSearchResponse(
                query,
                "NO_RELEVANT_MATCHES",
                "No sufficiently relevant historical resolutions were found.",
                List.of());
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
