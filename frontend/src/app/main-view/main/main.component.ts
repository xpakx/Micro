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
    /*this.postService.getPosts().subscribe({
      next: (response: Page<PostDetails>) => this.updateList(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });*/
    this.test();
  }

  showError(error: HttpErrorResponse): void {
    this.errorOccured = true;
    this.errorMsg = error.error.message;
  }

  updateList(response: Page<PostDetails>): void {
    this.postList = response;
    this.errorOccured = false;
  }

  test(): void {
    this.postList = {
      content: [{id: 0, content: "post", edited: false, createdAt: new Date(), user: {username: "Test", gender: "male", confirmed: true, avatarUrl: ""} }],
      pageable: {
          sort: {
              sorted: true,
              unsorted: true,
              empty: false
          },
          offset: 1,
          pageNumber: 1,
          pageSize: 1,
          paged: true,
          unpaged: true
      },
      totalPages: 1,
      last: true,
      number: 1,
      sort: {
          sorted: true,
          unsorted: true,
          empty: false
      },
      size: 1,
      numberOfElements: 1,
      first: true,
      empty: false
  }
  }
}
