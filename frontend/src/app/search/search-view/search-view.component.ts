import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommentListService } from 'src/app/comment/comment-list.service';
import { CommentDetails } from 'src/app/comment/dto/comment-details';
import { Page } from 'src/app/common/dto/page';
import { PostDetails } from 'src/app/post/dto/post-details';
import { PostListService } from 'src/app/post/post-list.service';

@Component({
  selector: 'app-search-view',
  templateUrl: './search-view.component.html',
  styleUrls: ['./search-view.component.css']
})
export class SearchViewComponent implements OnInit {
  showPosts: boolean = true;
  posts?: Page<PostDetails>;
  comments?: Page<CommentDetails>;

  constructor(private postService: PostListService, private commentService: CommentListService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.getResults(params['search']);
    });
  }

  getResults(search: String): void {
    this.getPosts(search);
  }

  private getPosts(search: String) {
    this.postService.search(search).subscribe({
      next: (response: Page<PostDetails>) => this.updatePosts(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
    //todo
  }

  updatePosts(response: Page<PostDetails>): void {
    this.posts = response;
    this.showPosts = true;
  }

  private getComments(search: String) {
    this.commentService.search(search).subscribe({
      next: (response: Page<CommentDetails>) => this.updateComments(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  updateComments(response: Page<CommentDetails>): void {
    this.comments = response;
    this.showPosts = false;
  }

}
