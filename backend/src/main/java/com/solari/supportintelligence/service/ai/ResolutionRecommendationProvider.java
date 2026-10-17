
package com.solari.supportintelligence.service.ai;

import java.util.List;

import com.solari.supportintelligence.dto.request.NewTicketRequest;
import com.solari.supportintelligence.dto.response.ResolutionRecommendation;
import com.solari.supportintelligence.dto.response.TicketSearchResult;

public interface ResolutionRecommendationProvider {

    String name();

    ResolutionRecommendation recommend(
            NewTicketRequest ticket,
            List<TicketSearchResult> matches);
}
