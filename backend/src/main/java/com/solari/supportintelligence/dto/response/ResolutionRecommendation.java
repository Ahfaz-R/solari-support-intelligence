
package com.solari.supportintelligence.dto.response;

import java.util.List;

public record ResolutionRecommendation(
        String status,
        String provider,
        String confidence,
        String recommendedResolution,
        String rationale,
        List<String> suggestedNextSteps,
        List<String> supportingTicketIds) {
}
