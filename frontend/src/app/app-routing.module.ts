import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginFormComponent } from './authentication/login-form/login-form.component';
import { LogoutComponent } from './authentication/logout/logout.component';
import { RegisterFormComponent } from './authentication/register-form/register-form.component';
import { ActiveComponent } from './main-view/active/active.component';
import { CommentEditViewComponent } from './main-view/comment-edit-view/comment-edit-view.component';
import { FavComponent } from './main-view/fav/fav.component';
import { FollowTagViewComponent } from './main-view/follow-tag-view/follow-tag-view.component';
import { FollowUserViewComponent } from './main-view/follow-user-view/follow-user-view.component';
import { HotComponent } from './main-view/hot/hot.component';
import { MainComponent } from './main-view/main/main.component';
import { PostEditViewComponent } from './main-view/post-edit-view/post-edit-view.component';
import { SinglePostComponent } from './main-view/single-post/single-post.component';
import { TagViewComponent } from './main-view/tag-view/tag-view.component';
import { UserViewComponent } from './main-view/user-view/user-view.component';
import { MentionListComponent } from './mention/mention-list/mention-list.component';

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
  { path: 'hot', component: HotComponent },
  { path: 'active', component: ActiveComponent },
  { path: 'fav', component: FavComponent },
  { path: 'my/tags', component: FollowTagViewComponent },
  { path: 'my/users', component: FollowUserViewComponent },
  { path: 'my/mentions', component: MentionListComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
