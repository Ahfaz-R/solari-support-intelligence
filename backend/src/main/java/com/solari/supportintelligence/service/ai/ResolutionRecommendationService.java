
package com.solari.supportintelligence.service.ai;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.solari.supportintelligence.dto.request.NewTicketRequest;
import com.solari.supportintelligence.dto.response.ResolutionRecommendation;
import com.solari.supportintelligence.dto.response.TicketSearchResult;

@Service
public class ResolutionRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(ResolutionRecommendationService.class);

    private final List<ResolutionRecommendationProvider> providers;
    private final RuleBasedResolutionRecommendationProvider fallbackProvider;
    private final String configuredProvider;

    public ResolutionRecommendationService(
            List<ResolutionRecommendationProvider> providers,
            RuleBasedResolutionRecommendationProvider fallbackProvider,
            @Value("${solari.ai.provider:rules}") String configuredProvider) {
        this.providers = providers;
        this.fallbackProvider = fallbackProvider;
        this.configuredProvider = configuredProvider;
    }

    public ResolutionRecommendation recommend(
            NewTicketRequest ticket,
            List<TicketSearchResult> matches) {

        ResolutionRecommendationProvider selectedProvider = findProvider(configuredProvider);

        if (selectedProvider == fallbackProvider) {
            return fallbackProvider.recommend(ticket, matches);
        }

        try {
            return selectedProvider.recommend(ticket, matches);
        } catch (RuntimeException exception) {
            log.warn(
                    "Recommendation provider '{}' failed for ticket '{}'. "
                            + "Falling back to rules-based recommendation.",
                    selectedProvider.name(),
                    ticket.ticketId(),
                    exception);

            ResolutionRecommendation fallback = fallbackProvider.recommend(ticket, matches);

            return new ResolutionRecommendation(
                    fallback.status(),
                    fallback.provider(),
                    fallback.confidence(),
                    fallback.recommendedResolution(),
                    "The configured AI provider failed. "
                            + "Using the rule-based fallback. "
                            + fallback.rationale(),
                    fallback.suggestedNextSteps(),
                    fallback.supportingTicketIds());
        }
    }

    private ResolutionRecommendationProvider findProvider(String name) {
        String normalizedName = name == null
                ? "rules"
                : name.trim().toLowerCase(Locale.ROOT);

        if (normalizedName.equals("rules")) {
            return fallbackProvider;
        }

        return providers.stream()
                .filter(provider -> provider.name().equalsIgnoreCase(normalizedName))
                .findFirst()
                .orElseGet(() -> {
                    log.warn(
                            "Unknown recommendation provider '{}'. "
                                    + "Using rules-based provider.",
                            normalizedName);
                    return fallbackProvider;
                });
    }
}
