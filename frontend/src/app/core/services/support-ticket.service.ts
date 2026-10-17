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

export interface NewTicketRequest {
  ticket_id: string;
  subject: string;
  description: string;
}

export interface TicketAnalysisRequest {
  new_tickets: NewTicketRequest[];
}

export interface ResolutionRecommendation {
  status: string;
  provider: string;
  confidence: string;
  recommendedResolution: string | null;
  rationale: string;
  suggestedNextSteps: string[];
  supportingTicketIds: string[];
}

export interface TicketAnalysisResult {
  ticketId: string;
  subject: string;
  description: string;
  matchStatus: string;
  message: string;
  matches: TicketSearchResult[];
  recommendation: ResolutionRecommendation;
}

export interface TicketAnalysisResponse {
  results: TicketAnalysisResult[];
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

  analyzeTickets(
    request: TicketAnalysisRequest
  ): Observable<TicketAnalysisResponse> {
    return this.http.post<TicketAnalysisResponse>(
      '/api/v1/tickets/analyze',
      request
    );
  }
}
