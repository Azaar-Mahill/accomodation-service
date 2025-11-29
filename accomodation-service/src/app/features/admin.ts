import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';

import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';

import { AccommodationService } from '../core/accommodation.service';
import { AccommodationType, EnvironmentType } from '../core/models';
import { ACCOMMODATIONS } from '../core/mock-accommodations';
import { Accommodation } from '../core/models';
import { InfoDialogComponent } from './info-dialog.component';
import { AuthService } from '../core/auth.service';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatTabsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatDialogModule,
    MatCardModule,
    BaseChartDirective,          // for baseChart on <canvas>
    MatCheckboxModule, 
  ],
  templateUrl: './admin.html',
  styleUrls: ['./admin.scss'],
})
export class AdminComponent {
  months = Array.from({ length: 12 }, (_, i) => ({
    value: i + 1,
    label: new Date(2000, i, 1).toLocaleString('en', { month: 'long' })
  }));
  environmentTypes: EnvironmentType[] = ['Any','Beach','Hill Country','City','Wildlife','Cultural'];
  accomodationTypes: AccommodationType[] = ['Any','Hotel','Resort','Villa','Guest House','Hostel'];

    provinces: string[] = [
    'Western',
    'Central',
    'Southern',
    'Northern',
    'Eastern',
    'North Western',
    'North Central',
    'Uva',
    'Sabaragamuwa'
  ];

  // ✅ districts by province (Sri Lanka)
  districtsByProvince: { [province: string]: string[] } = {
    Western: ['Colombo', 'Gampaha', 'Kalutara'],
    Central: ['Kandy', 'Matale', 'Nuwara Eliya'],
    Southern: ['Galle', 'Matara', 'Hambantota'],
    Northern: ['Jaffna', 'Kilinochchi', 'Mannar', 'Mullaitivu', 'Vavuniya'],
    Eastern: ['Trincomalee', 'Batticaloa', 'Ampara'],
    'North Western': ['Kurunegala', 'Puttalam'],
    'North Central': ['Anuradhapura', 'Polonnaruwa'],
    Uva: ['Badulla', 'Monaragala'],
    Sabaragamuwa: ['Ratnapura', 'Kegalle'],
  };


  // all mock data
  private allAccommodations: Accommodation[] = ACCOMMODATIONS;

  // declare with definite assignment; initialize in constructor
  tab4Form!: FormGroup;
  tab5Form!: FormGroup;

  tab4Results = signal<any[]>([]);
  tab5Results = signal<any[]>([]);

  private dialog = inject(MatDialog);

    // labels for the 12 months
  monthLabels: string[] = [
    'Jan','Feb','Mar','Apr','May','Jun',
    'Jul','Aug','Sep','Oct','Nov','Dec'
  ];


  bookingsBarOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { display: false }
    },
    scales: {
      x: {},
      y: {
        beginAtZero: true,
        max: 20,
        title: { display: true, text: '°C' }
      }
    }
  };

  temperatureBarOptions: ChartConfiguration['options'] = {
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


  getBookingsChartData(a: any): ChartConfiguration['data'] {
    const temps: number[] = this.monthLabels.map((_, idx) => {
      // avgTempByMonthC has keys 1..12
      return a?.bookingsByMonth?.[idx + 1] ?? 0;
    });

    return {
      labels: this.monthLabels,
      datasets: [
        {
          data: temps,
          label: 'Monthly bookings'
        }
      ]
    };
  }

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

  constructor(
    private fb: FormBuilder, 
    private svc: AccommodationService,
    private auth: AuthService, 
    private router: Router  
  ) {


    this.tab4Form = this.fb.group({
      province: [null as string | null],
      useDistrict: [false],
      district: [null as string | null],
    });

    this.tab5Form = this.fb.group({
      accomodationType: [null as AccommodationType | null]
    });
  }

  searchTab4(): void {
  const { province, useDistrict, district } = this.tab4Form.value as {
    province: string | null;
    useDistrict: boolean;
    district: string | null;
  };

  if (province != null) {
    if((useDistrict == true) && (district == null)){
      this.dialog.open(InfoDialogComponent, {
      data: {
        title: 'Selections required',
        message: 'Please select a district or uncheck the checkbox before searching.'
      }
    });
    }else{
      this.svc.KPIInformation({
        province: province ?? null,
        useDistrict: useDistrict ?? null,
        district: district ?? null
      }).subscribe({
        next: (list) => this.tab4Results.set(list), 
        error: (err) => {
          console.error('KPI search failed', err);
          this.tab4Results.set([]);
        }
      });
    }
    
  } else {
    this.dialog.open(InfoDialogComponent, {
      data: {
        title: 'Selections required',
        message: 'Please select a province before searching.'
      }
    });
  }
}



  searchTab5(): void {

    const { accomodationType } = this.tab5Form.value;

    if( accomodationType != null ){
      this.svc.accomodationTypeInformation({
        accomodationType: accomodationType ?? null
      }).subscribe({
        next: (list) => this.tab5Results.set(list),
        error: (err) => {
          console.error('accomodationType selection search failed', err);
          this.tab5Results.set([]);
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

    getDistrictsForSelectedProvince(): string[] {
    const province = this.tab4Form.get('province')?.value as string | null;
    if (!province) {
      return [];
    }
    return this.districtsByProvince[province] ?? [];
  }


  logout(): void {
    this.auth.logout();               // clear user info
    this.router.navigate(['/login']); // go back to login page
  }

}
