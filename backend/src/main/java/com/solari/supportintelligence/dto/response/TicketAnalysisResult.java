
package com.solari.supportintelligence.dto.response;

import java.util.List;

public record TicketAnalysisResult(
        String ticketId,
        String subject,
        String description,
        String matchStatus,
        String message,
        List<TicketSearchResult> matches,
        ResolutionRecommendation recommendation) {
}
