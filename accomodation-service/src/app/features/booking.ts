import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { AccommodationService } from '../core/accommodation.service';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule],
  template: `
    <div class="wrap" *ngIf="acc">
      <mat-card>
        <mat-card-title>Book: {{ acc.name }}</mat-card-title>
        <mat-card-content>
          <p><strong>Address:</strong> {{ acc.address }}</p>
          <p><strong>District:</strong> {{ acc.district }}</p>
          <p><strong>Type:</strong> {{ acc.accomodationType }} | <strong>Environment:</strong> {{ acc.environmentType }}</p>
        </mat-card-content>
        <mat-card-actions>
          <a *ngIf="acc.bookingUrl" mat-raised-button color="primary" [href]="acc.bookingUrl" target="_blank" rel="noopener">
            Proceed to Booking
          </a>
          <a mat-button color="accent" routerLink="/">Back</a>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [`.wrap{padding:1rem;display:flex;justify-content:center}`]
})
export class BookingComponent {
  private route = inject(ActivatedRoute);
  private svc = inject(AccommodationService);
  acc = this.svc.findById(this.route.snapshot.paramMap.get('id')!);
}
