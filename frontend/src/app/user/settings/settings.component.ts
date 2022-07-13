import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { UserDto } from '../dto/user-dto';
import { SettingsService } from '../settings.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  passwordForm: UntypedFormGroup;
  genderForm: UntypedFormGroup;

  constructor(private settingsService: SettingsService, private fb: UntypedFormBuilder) {
    this.passwordForm = this.fb.group({
      password: ['', Validators.required],
      passwordRe: ['', Validators.required]
    });
    this.genderForm = this.fb.group({
      gender: ['', Validators.required]
    });
   }

  ngOnInit(): void {
  }

  changePassword(): void {
    if(this.passwordForm.valid) {
      this.settingsService.changePassword({
        password: this.passwordForm.controls['password'].value,
        passwordRe: this.passwordForm.controls['passwordRe'].value,
      }).subscribe({
        next: (response: UserDto) => this.onSuccess(),
        error: (error: HttpErrorResponse) => this.showError(error)
      })
    }
  }

  private showError(error: HttpErrorResponse) {
    //todo
  }

  private onSuccess() {
    //todo
  }


  changeGender(): void {
    if(this.genderForm.valid) {
      this.settingsService.changeGender({
        gender: this.genderForm.controls['gender'].value
      }).subscribe({
        next: (response: UserDto) => this.onSuccess(),
        error: (error: HttpErrorResponse) => this.showError(error)
      })
    }
  }
}
