import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { CommentListService } from 'src/app/comment/comment-list.service';
import { SearchCommentDetails } from 'src/app/comment/dto/search-comment-details';
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
  comments?: Page<SearchCommentDetails>;
  searchForm: UntypedFormGroup;
  searchString: String = '';

  constructor(private postService: PostListService, private commentService: CommentListService, private route: ActivatedRoute, private fb: UntypedFormBuilder) {
    this.searchForm = this.fb.group({
      search: ['']
    });
   }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.getResults(params['search']);
    });
  }

  getResults(search: String): void {
    this.searchString = search;
    this.getPosts(search);
  }

  toPostPage(page: number): void {
    this.postService.search(this.searchString, page).subscribe({
      next: (response: Page<PostDetails>) => this.updatePosts(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
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

  toCommentPage(page: number) {
    this.commentService.search(this.searchString, page).subscribe({
      next: (response: Page<SearchCommentDetails>) => this.updateComments(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  private getComments(search: String) {
    this.commentService.search(search).subscribe({
      next: (response: Page<SearchCommentDetails>) => this.updateComments(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  updateComments(response: Page<SearchCommentDetails>): void {
    this.comments = response;
    this.showPosts = false;
  }

  switchSearchInPosts(): void {
    this.showPosts = !this.showPosts;
    if(this.showPosts) {
      this.getPosts(this.searchString);
    } else {
      this.getComments(this.searchString);
    }
  }

  search(): void {
    this.searchString = this.searchForm.controls['search'].value;
    if(this.showPosts) {
      this.getPosts(this.searchString);
    } else {
      this.getComments(this.searchString);
    }
    
  }
}
