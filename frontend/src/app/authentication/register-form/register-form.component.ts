import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from '../authentication.service';
import { Token } from '../dto/token';

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.css']
})
export class RegisterFormComponent implements OnInit {
  form: FormGroup;
  public invalid: boolean = false;
  public message: string = '';

  constructor(private service: AuthenticationService, private fb: FormBuilder) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      passwordRe: ['', Validators.required]
    });
  }

  ngOnInit(): void {
  }

  register(): void {
    if(this.form.valid) {
      this.invalid = false;
      this.service.register({
        username: this.form.controls['username'].value,
        password: this.form.controls['password'].value,
        passwordRe: this.form.controls['passwordRe'].value,
      }).subscribe({
        next: (response: Token) => this.saveToken(response),
        error: (error: HttpErrorResponse) => this.showError(error)
      })
    } else {
      this.message = "Fields cannot be empty!";
      this.invalid = true;
    }
  }

  private showError(error: HttpErrorResponse) {
    this.message = error.error.message;
    this.invalid = true;
  }

  private saveToken(response: Token) {
    localStorage.setItem("token", response.token);
    localStorage.setItem("username", response.username);
  }
}
