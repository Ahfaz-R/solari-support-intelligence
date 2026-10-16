package com.solari.supportintelligence.dto.response;

public record TicketImportResponse(
        int imported,
        int skipped) {
}
