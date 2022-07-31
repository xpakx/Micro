import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Page } from 'src/app/common/dto/page';
import { PostWithComments } from 'src/app/post/dto/post-with-comments';
import { PostListService } from 'src/app/post/post-list.service';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
  postList?: Page<PostWithComments>;
  errorOccured: boolean = false;
  errorMsg: String = '';

  constructor(private postService: PostListService, private route: ActivatedRoute, private router: Router) { }

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
    this.postService.getPosts().subscribe({
      next: (response: Page<PostWithComments>) => this.updateList(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  loadPage(page: number): void {
    this.postService.getPosts(page).subscribe({
      next: (response: Page<PostWithComments>) => this.updateList(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  toPage(page: number): void {
    this.router.navigate([`/page/${page}`]);
  }

  showError(error: HttpErrorResponse): void {
    this.errorOccured = true;
    this.errorMsg = error.error.message;
  }

  updateList(response: Page<PostWithComments>): void {
    this.postList = response;
    this.errorOccured = false;
  }

  addNewPost(post: PostWithComments): void {
    if(this.postList) {
      this.postList.content = [post].concat(this.postList.content);
    }
  }
}
