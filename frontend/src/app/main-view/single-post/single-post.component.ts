import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommentListService } from 'src/app/comment/comment-list.service';
import { CommentDetails } from 'src/app/comment/dto/comment-details';
import { Page } from 'src/app/common/dto/page';
import { PostDetails } from 'src/app/post/dto/post-details';
import { PostWithComments } from 'src/app/post/dto/post-with-comments';
import { PostListService } from 'src/app/post/post-list.service';

@Component({
  selector: 'app-single-post',
  templateUrl: './single-post.component.html',
  styleUrls: ['./single-post.component.css']
})
export class SinglePostComponent implements OnInit {
  post?: PostDetails;
  comments?: Page<CommentDetails>;

  constructor(private route: ActivatedRoute, private postService: PostListService, private commentService: CommentListService) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      this.loadPost(routeParams['id']);
    });   
  }

  loadPost(id: number) {
    this.postService.getPost(id).subscribe({
      next: (response: PostWithComments) => this.savePost(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
    
  }

  savePost(response: PostWithComments): void {
    this.post = response.post;
    this.comments = response.comments;
    this.comments.content.reverse();
  }
}
