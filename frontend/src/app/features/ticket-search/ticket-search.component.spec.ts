import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { TicketSearchComponent } from './ticket-search.component';

describe('TicketSearchComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketSearchComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(TicketSearchComponent);

    expect(fixture.componentInstance).toBeTruthy();
  });
});
