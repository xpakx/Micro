import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthorizedUserService } from 'src/app/user/authorized-user.service';
import { AuthenticationService } from '../authentication.service';
import { Token } from '../dto/token';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent implements OnInit {
  form: UntypedFormGroup;
  public invalid: boolean = false;
  public message: string = '';

  constructor(private service: AuthenticationService, private fb: UntypedFormBuilder, private loginService: AuthorizedUserService, private router: Router) {
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
    this.loginService.saveData(response);
    this.router.navigate(['/']);
  }
}
