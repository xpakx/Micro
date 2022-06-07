import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { PostDetails } from '../post/dto/post-details';
import { PostListService } from '../post/post-list.service';

@Component({
  selector: 'app-right-sidebar',
  templateUrl: './right-sidebar.component.html',
  styleUrls: ['./right-sidebar.component.css']
})
export class RightSidebarComponent implements OnInit {
  posts: PostDetails[] = [];

  constructor(private postService: PostListService) { }

  ngOnInit(): void {
    this.postService.getRandomHotPosts().subscribe({
      next: (response: PostDetails[]) => this.updatePosts(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
    
  }

  updatePosts(response: PostDetails[]): void {
    this.posts = response;
  }
}
