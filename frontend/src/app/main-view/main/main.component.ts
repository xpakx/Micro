import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Page } from 'src/app/common/dto/page';
import { PostDetails } from 'src/app/post/dto/post-details';
import { PostListService } from 'src/app/post/post-list.service';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
  postList?: Page<PostDetails>;
  errorOccured: boolean = false;
  errorMsg: String = '';

  constructor(private postService: PostListService) { }

  ngOnInit(): void {
    this.getPosts();
  }

  getPosts(): void {
    this.postService.getPosts().subscribe({
      next: (response: Page<PostDetails>) => this.updateList(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
    this.errorOccured = true;
    this.errorMsg = error.error.message;
  }

  updateList(response: Page<PostDetails>): void {
    this.postList = response;
    this.errorOccured = false;
  }
}
