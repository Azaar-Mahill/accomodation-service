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
      (params.environment ? a.environment === params.environment : true) &&
      (params.type ? a.type === params.type : true)
    );
  }

  // --- NEW: HTTP search for TAB1 ---
  searchBasicHttp(params: {
    month?: number | null;
    environment?: EnvironmentType | null;
    type?: AccommodationType | null;
  }): Observable<Accommodation[]> {
    let httpParams = new HttpParams();
    if (params.month != null) httpParams = httpParams.set('month', String(params.month));
    if (params.environment) httpParams = httpParams.set('environment', params.environment);
    if (params.type) httpParams = httpParams.set('environmentType', params.type);

    return this.http.get<Accommodation[]>(
      `${this.baseUrl}/api/accommodations/search`,
      { params: httpParams }
    );
  }

  // TAB2 (still local mock for measures)
  searchWithMeasures(params: {
    month: number;
    environment?: EnvironmentType;
    type?: AccommodationType;
  }): Array<Accommodation & { temperature: number; precipitation: number }> {
    const data = this.all();
    return data
      .filter(a =>
        (params.environment ? a.environment === params.environment : true) &&
        (params.type ? a.type === params.type : true)
      )
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
