import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  template: `
    <div class="top-bar">
      <span>Welcome to Tourism Accommodation Recommender</span>
      <span class="spacer"></span>
      <button mat-stroked-button color="warn" (click)="logout()">
        Logout
      </button>
    </div>

    <div style="padding: 16px">
      <h2>Admin page</h2>
      <p>Only admins can see this page.</p>
    </div>
  `,
  styles: [`
    .top-bar {
      display: flex;
      align-items: center;
      justify-content: flex-end;
      padding: 8px 16px;
    }
    .spacer {
      flex: 1;
    }
  `]
})
export class AdminComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  logout(): void {
    this.auth.logout();               // clear user info + token
    this.router.navigate(['/login']); // go back to login page
  }
}
