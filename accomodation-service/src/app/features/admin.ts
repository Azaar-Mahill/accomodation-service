import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="padding: 16px">
      <h2>Admin page</h2>
      <p>Only admins can see this page.</p>
    </div>
  `
})
export class AdminComponent {}
