import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
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
import { RightSidebarComponent } from './right-sidebar/right-sidebar.component';
import { FollowButtonComponent } from './follows/follow-button/follow-button.component';
import { FollowTagViewComponent } from './main-view/follow-tag-view/follow-tag-view.component';
import { FollowUserViewComponent } from './main-view/follow-user-view/follow-user-view.component';
import { MentionListComponent } from './mention/mention-list/mention-list.component';
import { AvatarComponent } from './post/avatar/avatar.component';
import { MessageFormComponent } from './message/message-form/message-form.component';
import { MessageButtonComponent } from './message/message-button/message-button.component';
import { MessageListComponent } from './message/message-list/message-list.component';
import { MessageComponent } from './message/message/message.component';
import { MessageViewComponent } from './main-view/message-view/message-view.component';
import { PaginationComponent } from './post/pagination/pagination.component';
import { MySidebarComponent } from './left-sidebar/my-sidebar/my-sidebar.component';
import { EmojiChoiceComponent } from './modal/emoji-choice/emoji-choice.component';
import { MainContainerComponent } from './main-view/main-container/main-container.component';
import { SearchViewComponent } from './search/search-view/search-view.component';
import { CommentSearchComponent } from './comment/comment-search/comment-search.component';
import { SettingsComponent } from './user/settings/settings.component';
import { MarkdownModule } from 'ngx-markdown';
import { TagifyPipe } from './common/tagify.pipe';
import { AttachmentChoiceComponent } from './common/attachment-choice/attachment-choice.component';
import { ReportFormComponent } from './moderation/report-form/report-form.component';
import { MyReportsComponent } from './moderation/my-reports/my-reports.component';
import { MyModeratedComponent } from './moderation/my-moderated/my-moderated.component';
import { ReportComponent } from './moderation/report/report.component';
import { ErrorInterceptor } from './error/error.interceptor';
import { ModerationPanelComponent } from './moderation/moderation-panel/moderation-panel.component';
import { ModerationFormComponent } from './moderation/moderation-form/moderation-form.component';

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
    FavComponent,
    RightSidebarComponent,
    FollowButtonComponent,
    FollowTagViewComponent,
    FollowUserViewComponent,
    MentionListComponent,
    AvatarComponent,
    MessageFormComponent,
    MessageButtonComponent,
    MessageListComponent,
    MessageComponent,
    MessageViewComponent,
    PaginationComponent,
    MySidebarComponent,
    EmojiChoiceComponent,
    MainContainerComponent,
    SearchViewComponent,
    CommentSearchComponent,
    SettingsComponent,
    TagifyPipe,
    AttachmentChoiceComponent,
    ReportFormComponent,
    MyReportsComponent,
    MyModeratedComponent,
    ReportComponent,
    ModerationPanelComponent,
    ModerationFormComponent
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
    FontAwesomeModule,
    MarkdownModule.forRoot()
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    }],
  bootstrap: [AppComponent]
})
export class AppModule { }
