import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {
  SupportTicketService,
  TicketAnalysisRequest,
  TicketAnalysisResult,
} from '../../core/services/support-ticket.service';

@Component({
  selector: 'app-ticket-analysis',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ticket-analysis.component.html',
  styleUrl: './ticket-analysis.component.scss',
})
export class TicketAnalysisComponent {
  private readonly ticketService = inject(SupportTicketService);

  readonly sampleJson = JSON.stringify(
    {
      new_tickets: [
        {
          ticket_id: 'NT-1',
          subject: "Can't log in, password reset email never arrives",
          description:
            'A customer requested a password reset an hour ago and says the email still has not arrived, even after checking spam.',
        },
        {
          ticket_id: 'NT-2',
          subject: 'Chart shows nothing for the revenue tab',
          description:
            'The revenue tab on the main dashboard is completely empty, with no bars or error message.',
        },
        {
          ticket_id: 'NT-3',
          subject: 'Getting charged twice this month',
          description:
            'The customer sees two charges for what should be a single monthly subscription payment.',
        },
        {
          ticket_id: 'NT-4',
          subject: 'Our data warehouse integration with Snowflake is failing',
          description:
            'A new Snowflake integration is failing with an unclear connection error.',
        },
      ],
    },
    null,
    2
  );

  newTicketsJson = this.sampleJson;

  readonly loading = signal(false);
  readonly error = signal('');
  readonly results = signal<TicketAnalysisResult[]>([]);
  readonly analyzed = signal(false);

  loadSample(): void {
    this.newTicketsJson = this.sampleJson;
    this.error.set('');
    this.results.set([]);
    this.analyzed.set(false);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      return;
    }

    if (!file.name.toLowerCase().endsWith('.json')) {
      this.error.set('Please select a JSON file.');
      input.value = '';
      return;
    }

    const reader = new FileReader();

    reader.onload = () => {
      if (typeof reader.result !== 'string') {
        this.error.set('Unable to read the selected file.');
        return;
      }

      this.newTicketsJson = reader.result;
      this.error.set('');
      this.results.set([]);
      this.analyzed.set(false);
    };

    reader.onerror = () => {
      this.error.set('Unable to read the selected file.');
    };

    reader.readAsText(file);
    input.value = '';
  }

  analyzeTickets(): void {
    this.error.set('');

    let request: TicketAnalysisRequest;

    try {
      request = this.parseRequest(this.newTicketsJson);
    } catch (error: unknown) {
      this.error.set(
        error instanceof Error ? error.message : 'The ticket JSON is invalid.'
      );
      return;
    }

    this.loading.set(true);
    this.results.set([]);
    this.analyzed.set(false);

    this.ticketService.analyzeTickets(request).subscribe({
      next: (response) => {
        this.results.set(response.results ?? []);
        this.analyzed.set(true);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        const body = err.error as {
          detail?: unknown;
          message?: unknown;
        } | null;

        const serverMessage =
          typeof body?.detail === 'string'
            ? body.detail
            : typeof body?.message === 'string'
            ? body.message
            : null;

        this.error.set(
          serverMessage ??
            'Unable to analyze tickets. Check that the backend is running and try again.'
        );

        this.loading.set(false);
      },
    });
  }

  private parseRequest(json: string): TicketAnalysisRequest {
    let parsed: unknown;

    try {
      parsed = JSON.parse(json);
    } catch {
      throw new Error('Invalid JSON. Check the format and try again.');
    }

    if (typeof parsed !== 'object' || parsed === null) {
      throw new Error('The JSON root must be an object.');
    }

    const root = parsed as { new_tickets?: unknown };
    const tickets = root.new_tickets;

    if (!Array.isArray(tickets) || tickets.length === 0) {
      throw new Error('Provide a non-empty "new_tickets" array.');
    }

    if (tickets.length > 50) {
      throw new Error('A maximum of 50 tickets can be analyzed per request.');
    }

    const newTickets = tickets.map((item: unknown, index: number) => {
      if (typeof item !== 'object' || item === null) {
        throw new Error(`Ticket ${index + 1} must be a JSON object.`);
      }

      const ticket = item as Record<string, unknown>;

      const ticketId =
        typeof ticket['ticket_id'] === 'string'
          ? ticket['ticket_id'].trim()
          : '';

      const subject =
        typeof ticket['subject'] === 'string' ? ticket['subject'].trim() : '';

      const description =
        typeof ticket['description'] === 'string'
          ? ticket['description'].trim()
          : '';

      if (!ticketId) {
        throw new Error(`Ticket ${index + 1} must have a ticket_id.`);
      }

      if (!subject && !description) {
        throw new Error(
          `Ticket ${ticketId} must have a subject or description.`
        );
      }

      return {
        ticket_id: ticketId,
        subject,
        description,
      };
    });

    return { new_tickets: newTickets };
  }
}
