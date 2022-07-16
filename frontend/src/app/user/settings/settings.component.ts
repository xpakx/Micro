import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
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

  file?: File;
  @ViewChild('avatarSelect', {static: true}) avatarSelect?: ElementRef;

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

  selectAvatar(event: Event) {
    const element = event.currentTarget as HTMLInputElement;
    let fileList: FileList | null = element.files;
    if(fileList && fileList.length > 0) {
      let firstFile = fileList.item(0);
      this.file = firstFile ? firstFile : undefined;
    }
  }

  openFileSelection() {
    if(this.avatarSelect) {
      this.avatarSelect.nativeElement.click();
    }
  }

  sendAvatar() {
    if(!this.file) {
      return;
    }
    this.settingsService.sendAvatar(this.file).subscribe(
      
    );
  }
}
