import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginFormComponent } from './authentication/login-form/login-form.component';
import { LogoutComponent } from './authentication/logout/logout.component';
import { RegisterFormComponent } from './authentication/register-form/register-form.component';
import { CommentEditViewComponent } from './main-view/comment-edit-view/comment-edit-view.component';
import { HotComponent } from './main-view/hot/hot.component';
import { MainComponent } from './main-view/main/main.component';
import { PostEditViewComponent } from './main-view/post-edit-view/post-edit-view.component';
import { SinglePostComponent } from './main-view/single-post/single-post.component';
import { TagViewComponent } from './main-view/tag-view/tag-view.component';
import { UserViewComponent } from './main-view/user-view/user-view.component';

const routes: Routes = [
  { path: '', component: MainComponent },
  { path: 'register', component: RegisterFormComponent },
  { path: 'login', component: LoginFormComponent },
  { path: 'logout', component: LogoutComponent },
  { path: 'post/:id', component: SinglePostComponent },
  { path: 'post/:id/edit', component: PostEditViewComponent },
  { path: 'comment/:id/edit', component: CommentEditViewComponent },
  { path: 'tag/:tag', component: TagViewComponent },
  { path: 'user/:name', component: UserViewComponent },
  { path: 'hot', component: HotComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
