import { Routes } from '@angular/router';
import { HomeComponent } from './features/home';
import { BookingComponent } from './features/booking';
import { LoginComponent } from './features/login';
import { AdminComponent } from './features/admin';
import { customerGuard, adminGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  // ✅ only customers can see Home + Book
  { path: '', component: HomeComponent, canActivate: [customerGuard] },
  { path: 'book/:id', component: BookingComponent, canActivate: [customerGuard] },

  // ✅ only admins can see admin page
  { path: 'admin', component: AdminComponent, canActivate: [adminGuard] },

  { path: '**', redirectTo: 'login' }
];
