import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { JwtModule } from '@auth0/angular-jwt';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginFormComponent } from './authentication/login-form/login-form.component';
import { RegisterFormComponent } from './authentication/register-form/register-form.component';
import { PostComponent } from './post/post/post.component';
import { GeneralPostListComponent } from './post/general-post-list/general-post-list.component';
import { MainComponent } from './main-view/main/main.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { PostFormComponent } from './post/post-form/post-form.component';
import { CommentComponent } from './comment/comment/comment.component';
import { GeneralCommentListComponent } from './comment/general-comment-list/general-comment-list.component';
import { LogoutComponent } from './authentication/logout/logout.component';
import { SinglePostComponent } from './main-view/single-post/single-post.component';
import { LikeButtonComponent } from './like/like-button/like-button.component';
import { FullPostFormComponent } from './post/full-post-form/full-post-form.component';
import { PostEditViewComponent } from './main-view/post-edit-view/post-edit-view.component';
import { TagViewComponent } from './main-view/tag-view/tag-view.component';
import { FullCommentFormComponent } from './comment/full-comment-form/full-comment-form.component';
import { CommentEditViewComponent } from './main-view/comment-edit-view/comment-edit-view.component';
import { UserViewComponent } from './main-view/user-view/user-view.component';
import { ModalDeleteComponent } from './modal/modal-delete/modal-delete.component';
import { LeftSidebarComponent } from './left-sidebar/left-sidebar/left-sidebar.component';
import { HotComponent } from './main-view/hot/hot.component';
import { ActiveComponent } from './main-view/active/active.component';
import { FavComponent } from './main-view/fav/fav.component';

export function tokenGetter() {
  return localStorage.getItem('token');
}

@NgModule({
  declarations: [
    AppComponent,
    LoginFormComponent,
    RegisterFormComponent,
    PostComponent,
    GeneralPostListComponent,
    MainComponent,
    PostFormComponent,
    CommentComponent,
    GeneralCommentListComponent,
    LogoutComponent,
    SinglePostComponent,
    LikeButtonComponent,
    FullPostFormComponent,
    PostEditViewComponent,
    TagViewComponent,
    FullCommentFormComponent,
    CommentEditViewComponent,
    UserViewComponent,
    ModalDeleteComponent,
    LeftSidebarComponent,
    HotComponent,
    ActiveComponent,
    FavComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
        allowedDomains: ['localhost:8080', '192.168.50.118:8080'],
      }
    }),
    FontAwesomeModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
