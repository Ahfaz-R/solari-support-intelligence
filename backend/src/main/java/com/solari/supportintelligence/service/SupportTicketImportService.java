
package com.solari.supportintelligence.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solari.supportintelligence.dto.request.TicketImportRequest;
import com.solari.supportintelligence.dto.request.TicketImportRequest.HistoricalTicket;
import com.solari.supportintelligence.dto.response.TicketImportResponse;
import com.solari.supportintelligence.entity.SupportTicket;
import com.solari.supportintelligence.repository.SupportTicketRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SupportTicketImportService {

    private final SupportTicketRepository repository;

    public SupportTicketImportService(SupportTicketRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TicketImportResponse importTickets(TicketImportRequest request) {
        if (request == null || request.tickets() == null) {
            throw new IllegalArgumentException(
                    "Request must contain a tickets array");
        }

        Set<String> seenIds = new HashSet<>();
        List<SupportTicket> toImport = new ArrayList<>();
        int skipped = 0;

        for (var item : request.tickets()) {
            if (item == null
                    || blank(item.ticketId())
                    || blank(item.category())
                    || blank(item.subject())
                    || blank(item.description())
                    || blank(item.resolutionNotes())
                    || item.createdAt() == null
                    || blank(item.status())) {
                throw new IllegalArgumentException(
                        "Every historical ticket must contain ticket_id, category, "
                                + "subject, description, resolution_notes, created_at and status");
            }

            if (!seenIds.add(item.ticketId())) {
                skipped++;
                continue;
            }

            if (repository.existsByTicketId(item.ticketId())) {
                skipped++;
                continue;
            }

            SupportTicket ticket = new SupportTicket();
            ticket.setTicketId(item.ticketId());
            ticket.setCategory(item.category());
            ticket.setSubject(item.subject());
            ticket.setDescription(item.description());
            ticket.setResolutionNotes(item.resolutionNotes());
            ticket.setTags(item.tags() == null
                    ? ""
                    : item.tags().stream()
                            .filter(tag -> tag != null && !tag.isBlank())
                            .collect(Collectors.joining(",")));
            ticket.setCreatedAt(item.createdAt());
            ticket.setStatus(item.status());

            toImport.add(ticket);
        }

        repository.saveAll(toImport);

        return new TicketImportResponse(toImport.size(), skipped);
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
