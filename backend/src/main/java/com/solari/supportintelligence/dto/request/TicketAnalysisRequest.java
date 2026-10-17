
package com.solari.supportintelligence.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TicketAnalysisRequest(
        @JsonProperty("new_tickets") List<NewTicketRequest> newTickets) {
}
