
package com.solari.supportintelligence.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NewTicketRequest(
        @JsonProperty("ticket_id") String ticketId,
        String subject,
        String description) {
}
