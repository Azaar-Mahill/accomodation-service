import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { AuthService, UserRole } from '../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule
  ],
  template: `
    <div class="login-container">
      <form [formGroup]="form" (ngSubmit)="onLogin()">
        <h2>Sign in</h2>

        <mat-form-field appearance="outline">
          <mat-label>Email</mat-label>
          <input matInput type="email" formControlName="email">
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Password</mat-label>
          <input matInput type="password" formControlName="password">
        </mat-form-field>

        <!-- Role is used when signing up -->
        <mat-form-field appearance="outline">
          <mat-label>Role for sign up</mat-label>
          <mat-select formControlName="role">
            <mat-option value="CUSTOMER">Customer</mat-option>
            <mat-option value="ADMIN">Admin</mat-option>
          </mat-select>
        </mat-form-field>

        <div class="buttons">
          <!-- ✅ Customer sign in button -->
          <button mat-raised-button color="primary" type="submit">
            Customer sign in
          </button>

          <!-- ✅ Sign up button: store new user in DB -->
          <button mat-stroked-button color="accent" type="button" (click)="onSignup()">
            Sign up
          </button>
        </div>

        <p class="error" *ngIf="error()">{{ error() }}</p>
        <p class="success" *ngIf="success()">{{ success() }}</p>
      </form>
    </div>
  `,
  styles: [`
    .login-container {
      max-width: 420px;
      margin: 40px auto;
    }
    form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .buttons {
      display: flex;
      gap: 12px;
      justify-content: space-between;
    }
    .error { color: red; font-size: 13px; }
    .success { color: green; font-size: 13px; }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  error = signal<string | null>(null);
  success = signal<string | null>(null);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
    role: ['CUSTOMER' as UserRole, [Validators.required]]  // default role
  });

  ngOnInit(): void {
    // if token already restored, send to correct page
    const role = this.auth.role();
    if (role === 'CUSTOMER') {
      this.router.navigateByUrl('/');
    } else if (role === 'ADMIN') {
      this.router.navigateByUrl('/admin');
    }
  }

  onLogin() {
    this.error.set(null);
    this.success.set(null);

    if (this.form.invalid) {
      this.error.set('Please fill email, password and role');
      return;
    }

    const { email, password } = this.form.value;

    this.auth.login(email!, password!)
      .subscribe({
        next: res => {
          if (res.role === 'CUSTOMER') {
            this.router.navigateByUrl('/');
          } else if (res.role === 'ADMIN') {
            this.router.navigateByUrl('/admin');
          }
        },
        error: err => {
          this.error.set(err.error?.message || 'Login failed');
        }
      });
  }

  onSignup() {
    this.error.set(null);
    this.success.set(null);

    if (this.form.invalid) {
      this.error.set('Please fill email, password and role');
      return;
    }

    const { email, password, role } = this.form.value;

    this.auth.signup(email!, password!, role as UserRole)
      .subscribe({
        next: () => {
          this.success.set('User created successfully. Now you can sign in.');
        },
        error: err => {
          this.error.set(err.error?.message || 'Sign up failed');
        }
      });
  }
}
