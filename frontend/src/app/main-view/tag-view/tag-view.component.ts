import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Page } from 'src/app/common/dto/page';
import { PostDetails } from 'src/app/post/dto/post-details';
import { PostListService } from 'src/app/post/post-list.service';

@Component({
  selector: 'app-tag-view',
  templateUrl: './tag-view.component.html',
  styleUrls: ['./tag-view.component.css']
})
export class TagViewComponent implements OnInit {
  postList?: Page<PostDetails>;
  errorOccured: boolean = false;
  errorMsg: String = '';

  constructor(private postService: PostListService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      this.getPosts(routeParams['tag']);
    }); 
  }

  getPosts(tag: String): void {
    this.postService.getPostsWithTag(tag).subscribe({
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
