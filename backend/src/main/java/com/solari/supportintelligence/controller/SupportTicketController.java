
package com.solari.supportintelligence.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.solari.supportintelligence.dto.request.TicketAnalysisRequest;
import com.solari.supportintelligence.dto.request.TicketImportRequest;
import com.solari.supportintelligence.dto.request.TicketSearchRequest;
import com.solari.supportintelligence.dto.response.TicketAnalysisResponse;
import com.solari.supportintelligence.dto.response.TicketImportResponse;
import com.solari.supportintelligence.dto.response.TicketSearchResponse;
import com.solari.supportintelligence.service.SupportTicketImportService;
import com.solari.supportintelligence.service.SupportTicketSearchService;
import com.solari.supportintelligence.service.TicketAnalysisService;

@RestController
@RequestMapping("/api/v1/tickets")
public class SupportTicketController {

    private final SupportTicketImportService importService;
    private final SupportTicketSearchService searchService;
    private final TicketAnalysisService analysisService;

    public SupportTicketController(
            SupportTicketImportService importService,
            SupportTicketSearchService searchService,
            TicketAnalysisService analysisService) {
        this.importService = importService;
        this.searchService = searchService;
        this.analysisService = analysisService;
    }

    @PostMapping("/import")
    public TicketImportResponse importTickets(
            @RequestBody TicketImportRequest request) {
        try {
            return importService.importTickets(request);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @PostMapping("/search")
    public TicketSearchResponse search(
            @RequestBody TicketSearchRequest request) {
        try {
            return searchService.search(request);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @PostMapping("/analyze")
    public TicketAnalysisResponse analyze(
            @RequestBody TicketAnalysisRequest request) {
        try {
            return analysisService.analyze(request);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }
}
