
package com.solari.supportintelligence.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record TicketImportRequest(
                List<HistoricalTicket> tickets) {
        public record HistoricalTicket(
                        @JsonProperty("ticket_id") String ticketId,
                        String category,
                        String subject,
                        String description,
                        @JsonProperty("resolution_notes") String resolutionNotes,
                        List<String> tags,
                        @JsonProperty("created_at") LocalDate createdAt,
                        String status) {
        }
}
