import { Routes } from '@angular/router';
import { TicketSearchComponent } from './features/ticket-search/ticket-search.component';

export const routes: Routes = [
  {
    path: '',
    component: TicketSearchComponent,
  },
  {
    path: '**',
    redirectTo: '',
  },
];
