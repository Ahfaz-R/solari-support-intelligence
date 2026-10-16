import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TicketSearchRequest {
  ticket_id: string;
  subject: string;
  description: string;
  limit?: number;
}

export interface TicketSearchResult {
  ticketId: string;
  category: string;
  subject: string;
  description: string;
  resolutionNotes: string;
  tags: string;
  createdAt: string;
  status: string;
  relevanceScore: number;
}

export interface TicketSearchResponse {
  query: string;
  matchStatus: string;
  message: string;
  matches: TicketSearchResult[];
}

@Injectable({
  providedIn: 'root',
})
export class SupportTicketService {
  private readonly http = inject(HttpClient);

  searchTickets(
    request: TicketSearchRequest
  ): Observable<TicketSearchResponse> {
    return this.http.post<TicketSearchResponse>(
      '/api/v1/tickets/search',
      request
    );
  }
}
