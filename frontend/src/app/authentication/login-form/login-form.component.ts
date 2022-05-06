import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from '../authentication.service';
import { Token } from '../dto/token';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent implements OnInit {
  form: FormGroup;
  public invalid: boolean = false;
  public message: string = '';

  constructor(private service: AuthenticationService, private fb: FormBuilder) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
   }

  ngOnInit(): void {
  }

  authenticate(): void {
    if(this.form.valid) {
      this.invalid = false;
      this.service.authenticate({
        username: this.form.controls['username'].value,
        password: this.form.controls['password'].value
      }).subscribe(
        (response: Token) => this.saveToken(response),
        (error: HttpErrorResponse) => this.showError(error)
      )
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
