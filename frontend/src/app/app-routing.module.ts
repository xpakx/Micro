import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginFormComponent } from './authentication/login-form/login-form.component';
import { LogoutComponent } from './authentication/logout/logout.component';
import { RegisterFormComponent } from './authentication/register-form/register-form.component';
import { MainComponent } from './main-view/main/main.component';
import { PostEditViewComponent } from './main-view/post-edit-view/post-edit-view.component';
import { SinglePostComponent } from './main-view/single-post/single-post.component';
import { TagViewComponent } from './main-view/tag-view/tag-view.component';

const routes: Routes = [
  { path: '', component: MainComponent },
  { path: 'register', component: RegisterFormComponent },
  { path: 'login', component: LoginFormComponent },
  { path: 'logout', component: LogoutComponent },
  { path: 'post/:id', component: SinglePostComponent },
  { path: 'post/:id/edit', component: PostEditViewComponent },
  { path: 'tag/:tag', component: TagViewComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
