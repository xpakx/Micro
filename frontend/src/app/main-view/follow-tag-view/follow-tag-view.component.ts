import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Page } from 'src/app/common/dto/page';
import { PostWithComments } from 'src/app/post/dto/post-with-comments';
import { PostService } from 'src/app/post/post.service';

@Component({
  selector: 'app-follow-tag-view',
  templateUrl: './follow-tag-view.component.html',
  styleUrls: ['./follow-tag-view.component.css']
})
export class FollowTagViewComponent implements OnInit {
  postList?: Page<PostWithComments>;
  errorOccured: boolean = false;
  errorMsg: String = '';

  constructor(private postService: PostService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      let page: number | undefined = routeParams['page'];
      if(page) {
        this.loadPage(page);
      } else {
        this.getPosts();
      }
    });
  }

  getPosts(): void {
    this.postService.getPostsWithFollowedTags().subscribe({
      next: (response: Page<PostWithComments>) => this.updateList(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  loadPage(page: number): void {
    this.postService.getPostsWithFollowedTags(page).subscribe({
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

  toPage(page: number): void {
    this.router.navigate([`/my/tags/page/${page}`]);
  }
}
