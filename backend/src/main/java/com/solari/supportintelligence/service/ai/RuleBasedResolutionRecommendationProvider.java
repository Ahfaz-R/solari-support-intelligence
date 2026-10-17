
package com.solari.supportintelligence.service.ai;

import java.util.List;

import org.springframework.stereotype.Component;

import com.solari.supportintelligence.dto.request.NewTicketRequest;
import com.solari.supportintelligence.dto.response.ResolutionRecommendation;
import com.solari.supportintelligence.dto.response.TicketSearchResult;

@Component
public class RuleBasedResolutionRecommendationProvider
        implements ResolutionRecommendationProvider {

    @Override
    public String name() {
        return "rules";
    }

    @Override
    public ResolutionRecommendation recommend(
            NewTicketRequest ticket,
            List<TicketSearchResult> matches) {

        if (matches == null || matches.isEmpty()) {
            return insufficientEvidence(
                    "No relevant historical tickets were found. "
                            + "Collect more details or investigate the issue manually.");
        }

        TicketSearchResult bestMatch = matches.stream()
                .filter(match -> match.resolutionNotes() != null)
                .filter(match -> !match.resolutionNotes().isBlank())
                .findFirst()
                .orElse(null);

        if (bestMatch == null) {
            return insufficientEvidence(
                    "Historical matches were found, but none contains "
                            + "recorded resolution notes.");
        }

        return new ResolutionRecommendation(
                "RECOMMENDATION_AVAILABLE",
                name(),
                "LOW",
                bestMatch.resolutionNotes(),
                "This suggestion is based on the highest-ranked historical "
                        + "ticket with recorded resolution notes: "
                        + bestMatch.ticketId()
                        + ". Verify that the symptoms and environment match "
                        + "before applying the resolution.",
                List.of(
                        "Review historical ticket " + bestMatch.ticketId()
                                + " and confirm that its symptoms match the new ticket.",
                        "Check the relevant logs and configuration before applying "
                                + "the recorded resolution."),
                List.of(bestMatch.ticketId()));
    }

    private ResolutionRecommendation insufficientEvidence(String rationale) {
        return new ResolutionRecommendation(
                "INSUFFICIENT_EVIDENCE",
                name(),
                "INSUFFICIENT",
                null,
                rationale,
                List.of(
                        "Collect more diagnostic information before recommending a resolution."),
                List.of());
    }
}
