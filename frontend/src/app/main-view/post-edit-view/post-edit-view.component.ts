import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PostDetails } from 'src/app/post/dto/post-details';
import { PostListService } from 'src/app/post/post-list.service';

@Component({
  selector: 'app-post-edit-view',
  templateUrl: './post-edit-view.component.html',
  styleUrls: ['./post-edit-view.component.css']
})
export class PostEditViewComponent implements OnInit {
  post?: PostDetails;

  constructor(private route: ActivatedRoute, private postService: PostListService) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      this.loadPost(routeParams['id']);
    });   
  }

  loadPost(id: number) {
    this.postService.getPostMin(id).subscribe({
      next: (response: PostDetails) => this.savePost(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
    
  }

  savePost(response: PostDetails): void {
    this.post = response;
  }
}
