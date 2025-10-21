import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { AccommodationService } from '../core/accommodation.service';
import { AccommodationType, EnvironmentType } from '../core/models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterModule,
    MatTabsModule, MatFormFieldModule, MatSelectModule, MatButtonModule, MatCardModule
  ],
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class HomeComponent {
  months = Array.from({ length: 12 }, (_, i) => ({
    value: i + 1,
    label: new Date(2000, i, 1).toLocaleString('en', { month: 'long' })
  }));
  environments: EnvironmentType[] = ['Beach','Hill Country','City','Wildlife','Cultural'];
  types: AccommodationType[] = ['Hotel','Resort','Villa','Guest House','Hostel'];

  // declare with definite assignment; initialize in constructor
  tab1Form!: FormGroup;
  tab2Form!: FormGroup;

  tab1Results = signal<any[]>([]);
  tab2Results = signal<any[]>([]);

  constructor(private fb: FormBuilder, private svc: AccommodationService) {
    this.tab1Form = this.fb.group({
      month: [null as number | null],
      environment: [null as EnvironmentType | null],
      type: [null as AccommodationType | null],
    });

    this.tab2Form = this.fb.group({
      month: [null as number | null],
      environment: [null as EnvironmentType | null],
      type: [null as AccommodationType | null],
    });
  }

  searchTab1() {
  const { month, environment, type } = this.tab1Form.value;

  this.svc.searchBasicHttp({
    month: month ?? null,
    environment: environment ?? null,
    type: type ?? null,
  }).subscribe({
    next: (list) => this.tab1Results.set(list),
    error: (err) => {
      console.error('Accommodation Seletion search failed', err);
      this.tab1Results.set([]); // fallback to empty if error
    }
  });
}


  searchTab2() {
    const { month, environment, type } = this.tab2Form.value;
    if (!month) { this.tab2Results.set([]); return; }
    this.tab2Results.set(this.svc.searchWithMeasures({
      month,
      environment: environment ?? undefined,
      type: type ?? undefined,
    }));
  }
}
