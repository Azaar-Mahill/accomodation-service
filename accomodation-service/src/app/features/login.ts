import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  template: `
    <div class="login-container">
      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <h2>Sign in</h2>

        <mat-form-field appearance="outline">
          <mat-label>Email</mat-label>
          <input matInput type="email" formControlName="email">
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Password</mat-label>
          <input matInput type="password" formControlName="password">
        </mat-form-field>

        <!-- ✅ Sign in button is for customers -->
        <button mat-raised-button color="primary" type="submit">
          Customer sign in
        </button>

        <p class="error" *ngIf="error()">{{ error() }}</p>
      </form>
    </div>
  `,
  styles: [`
    .login-container {
      max-width: 400px;
      margin: 40px auto;
    }
    form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .error {
      color: red;
      font-size: 13px;
    }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  error = signal<string | null>(null);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  onSubmit() {
    this.error.set(null);

    if (this.form.invalid) {
      this.error.set('Please fill email and password');
      return;
    }

    const { email, password } = this.form.value;
    const role = this.auth.login(email!, password!);

    if (role === 'customer') {
      // ✅ customers go to the page in your screenshot (Home)
      this.router.navigateByUrl('/');
    } else if (role === 'admin') {
      // ✅ admins go to separate page
      this.router.navigateByUrl('/admin');
    } else {
      this.error.set('Invalid credentials');
    }
  }
}
