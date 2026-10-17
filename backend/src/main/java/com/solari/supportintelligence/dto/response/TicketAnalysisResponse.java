
package com.solari.supportintelligence.dto.response;

import java.util.List;

public record TicketAnalysisResponse(
        List<TicketAnalysisResult> results) {
}
