import { Routes } from '@angular/router';

import { TicketSearchComponent } from './features/ticket-search/ticket-search.component';
import { TicketAnalysisComponent } from './features/ticket-analysis/ticket-analysis.component';

export const routes: Routes = [
  {
    path: '',
    component: TicketSearchComponent,
  },
  {
    path: 'analysis',
    component: TicketAnalysisComponent,
  },
  {
    path: '**',
    redirectTo: '',
  },
];
