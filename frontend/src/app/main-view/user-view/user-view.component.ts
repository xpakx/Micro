import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Page } from 'src/app/common/dto/page';
import { FollowedResponse } from 'src/app/follows/dto/followed-response';
import { FollowsService } from 'src/app/follows/follows.service';
import { PostWithComments } from 'src/app/post/dto/post-with-comments';
import { PostListService } from 'src/app/post/post-list.service';

@Component({
  selector: 'app-user-view',
  templateUrl: './user-view.component.html',
  styleUrls: ['./user-view.component.css']
})
export class UserViewComponent implements OnInit {
  postList?: Page<PostWithComments>;
  errorOccured: boolean = false;
  errorMsg: String = '';
  userName: String = '';
  followed: boolean = false;
  showMessageForm: boolean = false;

  constructor(private postService: PostListService, private followsService: FollowsService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      let page: number | undefined = routeParams['page'];
      if(page) {
        this.loadPage(routeParams['name'], page);
      } else {
        this.getPosts(routeParams['name']);
      }
    }); 
  }

  getPosts(user: String): void {
    this.userName = user;
    this.postService.getUserPosts(user).subscribe({
      next: (response: Page<PostWithComments>) => this.updateList(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
    
    this.followsService.isUserFollowed(user).subscribe({
      next: (response: FollowedResponse) => this.updateFollow(response.followed),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  loadPage(user: String, page: number): void {
    this.userName = user;
    this.postService.getUserPosts(user, page).subscribe({
      next: (response: Page<PostWithComments>) => this.updateList(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
    
    this.followsService.isUserFollowed(user).subscribe({
      next: (response: FollowedResponse) => this.updateFollow(response.followed),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
    this.errorOccured = true;
    this.errorMsg = error.error.message;
  }

  updateList(response: Page<PostWithComments>): void {
    this.postList = response;
    this.errorOccured = false;
  }

  follow(follow: boolean) {
    if(follow) {
      this.followUser();
    } else {
      this.unfollowUser();
    }
  }

  followUser() {
    this.followsService.followUser({name: this.userName}).subscribe({
      next: (response: any) => this.updateFollow(true),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  updateFollow(followed: boolean): void {
    this.followed = followed;
  }

  unfollowUser() {
    this.followsService.unfollowUser(this.userName).subscribe({
      next: (response: any) => this.updateFollow(false),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  switchMessage() {
    this.showMessageForm = !this.showMessageForm;
  }
}
