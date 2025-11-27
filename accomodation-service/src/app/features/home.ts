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
import { BaseChartDirective } from 'ng2-charts';          // ✅ instead of NgChartsModule
import { ChartConfiguration } from 'chart.js';
import { ACCOMMODATIONS } from '../core/mock-accommodations';
import { Accommodation } from '../core/models';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterModule,
    MatTabsModule, MatFormFieldModule, MatSelectModule, MatButtonModule, MatCardModule,
    MatDialogModule, BaseChartDirective,
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

  // all mock data
  private allAccommodations: Accommodation[] = ACCOMMODATIONS;

  // declare with definite assignment; initialize in constructor
  tab1Form!: FormGroup;
  tab2Form!: FormGroup;
  tab3Form!: FormGroup;

  tab1Results = signal<any[]>([]);
  tab2Results = signal<any[]>([]);
  tab3Results = signal<any[]>([]);

  private dialog = inject(MatDialog);

    // labels for the 12 months
  monthLabels: string[] = [
    'Jan','Feb','Mar','Apr','May','Jun',
    'Jul','Aug','Sep','Oct','Nov','Dec'
  ];

  // Chart options used in the template
  barOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { display: false }
    },
    scales: {
      x: {},
      y: {
        beginAtZero: true,
        max: 60,
        title: { display: true, text: '°C' }
      }
    }
  };

  // Build chart data for one accommodation
  getTempChartData(a: any): ChartConfiguration['data'] {
    const temps: number[] = this.monthLabels.map((_, idx) => {
      // avgTempByMonthC has keys 1..12
      return a?.avgTempByMonthC?.[idx + 1] ?? 0;
    });

    return {
      labels: this.monthLabels,
      datasets: [
        {
          data: temps,
          label: 'Avg Temperature (°C)'
        }
      ]
    };
  }


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

    this.tab3Form = this.fb.group({
      accomodationType: [null as AccommodationType | null]
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
    const { month } = this.tab2Form.value;

    if( month != null ){
      this.svc.searchBasedOnWeather({
        month: month ?? null
      }).subscribe({
        next: (list) => this.tab2Results.set(list),
        error: (err) => {
          console.error('Month selection search failed', err);
          this.tab2Results.set([]);
        }
      });
    }else{
      //open a popup asking to select values in drop downs
      this.dialog.open(InfoDialogComponent, {
        data: {
          title: 'Selections required',
          message: 'Please select Month before searching.'
        }
      });
    }
  }

  getWeatherClass(status: string | undefined): string {
    switch (status) {
      case 'Super':
        return 'weather-super';
      case 'Normal':
        return 'weather-normal';
      case 'Bad':
        return 'weather-bad';
      default:
        return '';
    }
  }

    // Search for Tab 3 (Type of Accommodation)
  searchTab3(): void {

    const { accomodationType } = this.tab3Form.value;

    if( accomodationType != null ){
      this.svc.accomodationTypeInformation({
        accomodationType: accomodationType ?? null
      }).subscribe({
        next: (list) => this.tab3Results.set(list),
        error: (err) => {
          console.error('accomodationType selection search failed', err);
          this.tab3Results.set([]);
        }
      });
    }else{
      //open a popup asking to select values in drop downs
      this.dialog.open(InfoDialogComponent, {
        data: {
          title: 'Selections required',
          message: 'Please select accomodationType before searching.'
        }
      });
    }
  }



}
