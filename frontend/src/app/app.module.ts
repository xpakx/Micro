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
    LikeButtonComponent
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
