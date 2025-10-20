import { Injectable, signal } from '@angular/core';
import { ACCOMMODATIONS } from './mock-accommodations';
import { Accommodation, AccommodationType, EnvironmentType } from './models';

@Injectable({ providedIn: 'root' })
export class AccommodationService {
  private all = signal<Accommodation[]>(ACCOMMODATIONS);

  searchBasic(params: {
    month?: number;
    environment?: EnvironmentType;
    type?: AccommodationType;
  }): Accommodation[] {
    const data = this.all();
    return data.filter(a =>
      (params.environment ? a.environment === params.environment : true) &&
      (params.type ? a.type === params.type : true)
    );
  }

  // Returns measures for TAB2 (month required)
  searchWithMeasures(params: {
    month: number;
    environment?: EnvironmentType;
    type?: AccommodationType;
  }): Array<Accommodation & { temperature: number; precipitation: number }> {
    const data = this.all();
    return data
      .map(a => ({
        ...a,
        temperature: a.avgTempByMonthC[params.month],
        precipitation: a.avgPrecipByMonthMm[params.month],
      }));
  }

  findById(id: string) {
    return this.all().find(a => a.id === id) || null;
  }
}
