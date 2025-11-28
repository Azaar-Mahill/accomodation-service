import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export type UserRole = 'CUSTOMER' | 'ADMIN';

export interface User {
  email: string;
  role: UserRole;
}

export interface LoginResponse {
  email: string;
  role: UserRole;
  token: string;  
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  private _user = signal<User | null>(null);
  user = computed(() => this._user());
  role = computed<UserRole | null>(() => this._user()?.role ?? null);

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { email, password })
      .pipe(
        tap(res => {
          this._user.set({ email: res.email, role: res.role });
          localStorage.setItem('jwt', res.token); 
        })
      );
  }

  signup(email: string, password: string, role: UserRole): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/signup`, { email, password, role });
  }

  getToken(): string | null {
    return localStorage.getItem('jwt');
  }

  isCustomer() { return this.role() === 'CUSTOMER'; }
  isAdmin() { return this.role() === 'ADMIN'; }

  logout() {
    this._user.set(null);
    localStorage.removeItem('jwt');  
  }
}
