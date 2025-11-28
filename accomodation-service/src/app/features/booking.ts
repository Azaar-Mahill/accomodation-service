import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { AccommodationService } from '../core/accommodation.service';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

// This type matches what you display in template (accommodationName, etc.)
interface BookingViewModel {
  id: string;
  accommodationName: string;
  accommodationAddress: string;
  accommodationType: string;
  environment: string;
  bookingUrl?: string;
}

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, RouterModule],
  template: `
    <!-- This runs as SOON as you route to /book/:id -->
    <div class="wrap" *ngIf="acc$ | async as acc">
      <mat-card>
        <mat-card-title>Accomodation: {{ acc.accommodationName }}</mat-card-title>

        <mat-card-content>
          <p><strong>Address:</strong> {{ acc.accommodationAddress }}</p>
          <p><strong>Type:</strong> {{ acc.accommodationType }}</p>
          <p><strong>Environment:</strong> {{ acc.environment }}</p>
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

  // Observable that updates whenever the :id in the URL changes
  acc$: Observable<BookingViewModel | undefined> = this.route.paramMap.pipe(
    switchMap(params => {
      const id = params.get('id')!;
      console.log('📌 Route id:', id);
      return this.svc.findById(id); // must return Observable<BookingViewModel | undefined>
    })
  );
}
