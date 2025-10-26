import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { AccommodationService } from '../core/accommodation.service';
import { AccommodationType, EnvironmentType } from '../core/models';
import { InfoDialogComponent } from './info-dialog.component';
import { inject } from '@angular/core';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterModule,
    MatTabsModule, MatFormFieldModule, MatSelectModule, MatButtonModule, MatCardModule,
    MatDialogModule
  ],
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class HomeComponent {
  months = Array.from({ length: 12 }, (_, i) => ({
    value: i + 1,
    label: new Date(2000, i, 1).toLocaleString('en', { month: 'long' })
  }));
  environmentTypes: EnvironmentType[] = ['Any','Beach','Hill Country','City','Wildlife','Cultural'];
  accomodationTypes: AccommodationType[] = ['Any','Hotel','Resort','Villa','Guest House','Hostel'];

  // declare with definite assignment; initialize in constructor
  tab1Form!: FormGroup;
  tab2Form!: FormGroup;

  tab1Results = signal<any[]>([]);
  tab2Results = signal<any[]>([]);

  private dialog = inject(MatDialog);

  constructor(private fb: FormBuilder, private svc: AccommodationService) {
    this.tab1Form = this.fb.group({
      month: [null as number | null],
      environmentType: [null as EnvironmentType | null],
      accomodationType: [null as AccommodationType | null],
    });

    this.tab2Form = this.fb.group({
      month: [null as number | null],
      environmentType: [null as EnvironmentType | null],
      accomodationType: [null as AccommodationType | null],
    });
  }

  searchTab1() {
    const { month, environmentType, accomodationType } = this.tab1Form.value;

    if( month != null && environmentType != null && accomodationType != null){
      this.svc.searchBasicHttp({
        month: month ?? null,
        environmentType: environmentType ?? null,      // map -> environment
        accomodationType: accomodationType ?? null,            // map -> type
      }).subscribe({
        next: (list) => this.tab1Results.set(list),
        error: (err) => {
          console.error('Accommodation selection search failed', err);
          this.tab1Results.set([]);
        }
      });
    }else{
      //open a popup asking to select values in drop downs
      this.dialog.open(InfoDialogComponent, {
        data: {
          title: 'Selections required',
          message: 'Please select Month, Environment, and Accommodation Type before searching.'
        }
      });
    }

    
  }

  searchTab2() {
    /*const { month, environmentType, accomodationType } = this.tab2Form.value;
    if (!month) { this.tab2Results.set([]); return; }

    this.svc.searchWithMeasures({
      month,
      environment: environmentType ?? null,      // map -> environment
      type: accomodationType ?? null,            // map -> type
    }).subscribe({
      next: (list) => this.tab2Results.set(list),
      error: (err) => {
        console.error('Accommodation measures search failed', err);
        this.tab2Results.set([]);
      }
    });*/
  }

}
