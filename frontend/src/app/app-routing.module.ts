import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginFormComponent } from './authentication/login-form/login-form.component';
import { RegisterFormComponent } from './authentication/register-form/register-form.component';
import { MainComponent } from './main-view/main/main.component';

const routes: Routes = [
  { path: '', component: MainComponent },
  { path: 'register', component: RegisterFormComponent },
  { path: 'login', component: LoginFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
