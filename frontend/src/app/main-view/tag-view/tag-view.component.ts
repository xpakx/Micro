import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { faTag, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { Page } from 'src/app/common/dto/page';
import { FollowsService } from 'src/app/follows/follows.service';
import { PostWithComments } from 'src/app/post/dto/post-with-comments';
import { PostListService } from 'src/app/post/post-list.service';

@Component({
  selector: 'app-tag-view',
  templateUrl: './tag-view.component.html',
  styleUrls: ['./tag-view.component.css']
})
export class TagViewComponent implements OnInit {
  postList?: Page<PostWithComments>;
  errorOccured: boolean = false;
  errorMsg: String = '';
  tagName: String = '';
  faTag: IconDefinition = faTag;
  followed: boolean = false;

  constructor(private postService: PostListService, private followsService: FollowsService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      this.getPosts(routeParams['tag']);
    }); 
  }

  getPosts(tag: String): void {
    this.tagName = tag;
    this.postService.getPostsWithTag(tag).subscribe({
      next: (response: Page<PostWithComments>) => this.updateList(response),
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
      this.followTag();
    } else {
      this.unfollowTag();
    }
  }

  followTag() {
    this.followsService.followTag({name: this.tagName}).subscribe({
      next: (response: any) => this.updateFollow(true),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  updateFollow(followed: boolean): void {
    this.followed = followed;
  }

  unfollowTag() {
    this.followsService.unfollowTag(this.tagName).subscribe({
      next: (response: any) => this.updateFollow(false),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }
}
