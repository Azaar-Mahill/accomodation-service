import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ACCOMMODATIONS } from './mock-accommodations';
import { Accommodation, AccommodationType, EnvironmentType } from './models';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AccommodationService {
  private all = signal<Accommodation[]>(ACCOMMODATIONS);
  private baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  // --- OLD local filtering kept for reference ---
  searchBasicLocal(params: {
    month?: number;
    environment?: EnvironmentType;
    type?: AccommodationType;
  }): Accommodation[] {
    const data = this.all();
    return data.filter(a =>
      (params.environment ? a.environmentType === params.environment : true) &&
      (params.type ? a.accomodationType === params.type : true)
    );
  }

  // --- NEW: HTTP search for TAB1 ---
  searchBasicHttp(params: {
    month?: number | null;
    environmentType?: EnvironmentType | null;
    accomodationType?: AccommodationType | null;
  }): Observable<Accommodation[]> {
    let httpParams = new HttpParams();
    if (params.month != null) httpParams = httpParams.set('month', String(params.month));
    if (params.environmentType) httpParams = httpParams.set('environmentType', params.environmentType);
    if (params.accomodationType) httpParams = httpParams.set('accomodationType', params.accomodationType);

    return this.http.get<Accommodation[]>(
      `${this.baseUrl}/api/accommodations/search`,
      { params: httpParams }
    );
  }

  // TAB2 (still local mock for measures)
   searchBasedOnWeather(params: {
    month?: number | null;
  }): Observable<Accommodation[]> {
    let httpParams = new HttpParams();
    if (params.month != null) httpParams = httpParams.set('month', String(params.month));

    return this.http.get<Accommodation[]>(
      `${this.baseUrl}/api/accommodations/weather`,
      { params: httpParams }
    );
  }

  findById(id: string) {
    return this.all().find(a => a.id === id) || null;
  }
}
