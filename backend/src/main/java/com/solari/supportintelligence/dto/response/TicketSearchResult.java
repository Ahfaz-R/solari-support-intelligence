
package com.solari.supportintelligence.dto.response;

import java.time.LocalDate;

public record TicketSearchResult(
        String ticketId,
        String category,
        String subject,
        String description,
        String resolutionNotes,
        String tags,
        LocalDate createdAt,
        String status,
        double relevanceScore) {
}
