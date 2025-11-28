// src/app/core/auth.service.ts
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

  constructor(private http: HttpClient) {
    // 🔹 try to restore user when service is constructed
    this.restoreFromStoredToken();
  }

  /** Helper: are we in the browser (not server)? */
  private isBrowser(): boolean {
    return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined';
  }

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { email, password })
      .pipe(
        tap(res => {
          this._user.set({ email: res.email, role: res.role });
          if (this.isBrowser()) {
            localStorage.setItem('jwt', res.token);
          }
        })
      );
  }

  signup(email: string, password: string, role: UserRole): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/signup`, { email, password, role });
  }

  getToken(): string | null {
    if (!this.isBrowser()) return null;
    return localStorage.getItem('jwt');
  }

  // ✅ restore user from stored JWT on page load/refresh (browser only)
  restoreFromStoredToken(): void {
    if (!this.isBrowser()) {
      return; // in SSR there is no localStorage
    }

    const token = this.getToken();
    if (!token) return;

    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        this.logout();
        return;
      }

      const payload = JSON.parse(atob(parts[1])); // decode middle part
      const email = payload.sub as string;
      const role = payload.role as UserRole;
      const expMs = (payload.exp as number) * 1000; // seconds → ms

      if (Date.now() > expMs) {
        // token expired
        this.logout();
        return;
      }

      this._user.set({ email, role });
    } catch (e) {
      console.error('Failed to restore from token', e);
      this.logout();
    }
  }

  logout() {
    this._user.set(null);
    if (this.isBrowser()) {
      localStorage.removeItem('jwt');
    }
  }

  isCustomer() { return this.role() === 'CUSTOMER'; }
  isAdmin() { return this.role() === 'ADMIN'; }
}
