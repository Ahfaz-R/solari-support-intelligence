import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { TicketAnalysisComponent } from './ticket-analysis.component';
import { SupportTicketService } from '../../core/services/support-ticket.service';

describe('TicketAnalysisComponent', () => {
  let component: TicketAnalysisComponent;
  let fixture: ComponentFixture<TicketAnalysisComponent>;

  const ticketServiceMock = {
    analyzeTickets: jasmine
      .createSpy('analyzeTickets')
      .and.returnValue(of({ results: [] })),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketAnalysisComponent],
      providers: [
        {
          provide: SupportTicketService,
          useValue: ticketServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TicketAnalysisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
