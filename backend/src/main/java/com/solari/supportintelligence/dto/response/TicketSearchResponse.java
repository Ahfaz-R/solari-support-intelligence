
package com.solari.supportintelligence.dto.response;

import java.util.List;

public record TicketSearchResponse(
        String query,
        String matchStatus,
        String message,
        List<TicketSearchResult> matches) {
}
