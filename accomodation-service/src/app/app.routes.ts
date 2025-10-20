import { Routes } from '@angular/router';
import { HomeComponent } from './features/home';
import { BookingComponent } from './features/booking';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'book/:id', component: BookingComponent },
  { path: '**', redirectTo: '' }
];
