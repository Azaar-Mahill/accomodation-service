import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

type DialogData = { title?: string; message?: string };

@Component({
  selector: 'app-info-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ data.title || 'Missing selections' }}</h2>
    <div mat-dialog-content>
      <p>{{ data.message || 'Please select values in all dropdowns.' }}</p>
    </div>
    <div mat-dialog-actions align="end">
      <button mat-raised-button color="primary" (click)="close()">OK</button>
    </div>
  `
})
export class InfoDialogComponent {
  private dialogRef = inject(MatDialogRef<InfoDialogComponent>);
  // not optional here since you always pass data
  data = inject<DialogData>(MAT_DIALOG_DATA);

  close() { this.dialogRef.close(); }
}
