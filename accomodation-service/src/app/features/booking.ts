import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';               // ✅ for routerLink
import { AccommodationService } from '../core/accommodation.service';
import { Observable } from 'rxjs';
import { Accommodation4 } from '../core/models';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, RouterModule],
  template: `
    <!-- acc$ | async as acc gives you ONE Accommodation4 -->
    <div class="wrap" *ngIf="acc$ | async as acc">
      <mat-card>
        <mat-card-title>Book: {{ acc.name }}</mat-card-title>
        <mat-card-content>
          <p><strong>Address:</strong> {{ acc.address }}</p>
          <p><strong>District:</strong> {{ acc.district }}</p>
          <p>
            <strong>Type:</strong> {{ acc.accomodationType }}
            |
            <strong>Environment:</strong> {{ acc.environmentType }}
          </p>
        </mat-card-content>
        <mat-card-actions>
          <a *ngIf="acc.bookingUrl"
             mat-raised-button
             color="primary"
             [href]="acc.bookingUrl"
             target="_blank"
             rel="noopener">
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

  // ✅ clearly typed as an Observable of a single Accommodation4
  acc$: Observable<Accommodation4 | undefined>;

  constructor() {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.acc$ = this.svc.findById(id);
  }
}
