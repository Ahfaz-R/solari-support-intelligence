
package com.solari.supportintelligence.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solari.supportintelligence.dto.request.NewTicketRequest;
import com.solari.supportintelligence.dto.request.TicketAnalysisRequest;
import com.solari.supportintelligence.dto.request.TicketSearchRequest;
import com.solari.supportintelligence.dto.response.ResolutionRecommendation;
import com.solari.supportintelligence.dto.response.TicketAnalysisResponse;
import com.solari.supportintelligence.dto.response.TicketAnalysisResult;
import com.solari.supportintelligence.dto.response.TicketSearchResponse;
import com.solari.supportintelligence.service.ai.ResolutionRecommendationService;

@Service
public class TicketAnalysisService {

    private static final int MAX_BATCH_SIZE = 50;
    private static final int DEFAULT_MATCH_LIMIT = 5;

    private final SupportTicketSearchService searchService;
    private final ResolutionRecommendationService recommendationService;

    public TicketAnalysisService(
            SupportTicketSearchService searchService,
            ResolutionRecommendationService recommendationService) {
        this.searchService = searchService;
        this.recommendationService = recommendationService;
    }

    @Transactional(readOnly = true)
    public TicketAnalysisResponse analyze(TicketAnalysisRequest request) {
        if (request == null
                || request.newTickets() == null
                || request.newTickets().isEmpty()) {
            throw new IllegalArgumentException(
                    "Provide at least one new ticket");
        }

        if (request.newTickets().size() > MAX_BATCH_SIZE) {
            throw new IllegalArgumentException(
                    "A maximum of " + MAX_BATCH_SIZE
                            + " tickets can be analyzed per request");
        }

        List<TicketAnalysisResult> results = request.newTickets()
                .stream()
                .map(this::analyzeTicket)
                .toList();

        return new TicketAnalysisResponse(results);
    }

    private TicketAnalysisResult analyzeTicket(NewTicketRequest ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException(
                    "Ticket entries cannot be null");
        }

        if (isBlank(ticket.ticketId())) {
            throw new IllegalArgumentException(
                    "Every new ticket must have a ticket_id");
        }

        if (isBlank(ticket.subject()) && isBlank(ticket.description())) {
            throw new IllegalArgumentException(
                    "Ticket " + ticket.ticketId()
                            + " must have a subject or description");
        }

        TicketSearchRequest searchRequest = new TicketSearchRequest(
                ticket.ticketId(),
                ticket.subject(),
                ticket.description(),
                DEFAULT_MATCH_LIMIT);

        TicketSearchResponse searchResponse = searchService.search(searchRequest);

        ResolutionRecommendation recommendation = recommendationService.recommend(
                ticket,
                searchResponse.matches());

        return new TicketAnalysisResult(
                ticket.ticketId(),
                ticket.subject(),
                ticket.description(),
                searchResponse.matchStatus(),
                searchResponse.message(),
                searchResponse.matches(),
                recommendation);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
