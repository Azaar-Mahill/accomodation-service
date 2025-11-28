import { Injectable, signal, computed } from '@angular/core';

export type UserRole = 'customer' | 'admin';

export interface User {
  email: string;
  role: UserRole;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private _user = signal<User | null>(null);

  // expose read-only user + role
  user = computed(() => this._user());
  role = computed<UserRole | null>(() => this._user()?.role ?? null);

  // very simple demo login – replace with real API later
  login(email: string, password: string): UserRole | null {
    // example hard-coded users
    if (email === 'customer@example.com' && password === '123456') {
      this._user.set({ email, role: 'customer' });
      return 'customer';
    }

    if (email === 'admin@example.com' && password === '123456') {
      this._user.set({ email, role: 'admin' });
      return 'admin';
    }

    return null;
  }

  logout() {
    this._user.set(null);
  }

  isCustomer() {
    return this.role() === 'customer';
  }

  isAdmin() {
    return this.role() === 'admin';
  }
}
