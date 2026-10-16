import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  SupportTicketService,
  TicketSearchResult,
} from '../../core/services/support-ticket.service';

@Component({
  selector: 'app-ticket-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ticket-search.component.html',
  styleUrl: './ticket-search.component.scss',
})
export class TicketSearchComponent {
  private readonly ticketService = inject(SupportTicketService);

  ticketId = '';
  subject = '';
  description = '';

  readonly loading = signal(false);
  readonly error = signal('');
  readonly message = signal('');
  readonly query = signal('');
  readonly matchStatus = signal('');
  readonly results = signal<TicketSearchResult[]>([]);
  readonly searched = signal(false);

  searchTickets(): void {
    const ticketId = this.ticketId.trim();
    const subject = this.subject.trim();
    const description = this.description.trim();

    if (!ticketId && !subject && !description) {
      this.error.set('Enter a ticket ID, subject, or description to search.');
      return;
    }

    this.loading.set(true);
    this.error.set('');
    this.message.set('');
    this.query.set('');
    this.matchStatus.set('');
    this.results.set([]);
    this.searched.set(false);

    this.ticketService
      .searchTickets({
        ticket_id: ticketId,
        subject,
        description,
        limit: 5,
      })
      .subscribe({
        next: (response) => {
          this.query.set(response.query);
          this.matchStatus.set(response.matchStatus);
          this.message.set(response.message);
          this.results.set(response.matches ?? []);
          this.searched.set(true);
          this.loading.set(false);
        },
        error: (err: { error?: { message?: string }; message?: string }) => {
          this.error.set(
            err.error?.message ??
              'Unable to search historical tickets. Please try again.'
          );
          this.loading.set(false);
        },
      });
  }

  resetSearch(): void {
    this.ticketId = '';
    this.subject = '';
    this.description = '';
    this.loading.set(false);
    this.error.set('');
    this.message.set('');
    this.query.set('');
    this.matchStatus.set('');
    this.results.set([]);
    this.searched.set(false);
  }
}
