import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { faCheckCircle, faPaperclip, faPaperPlane, faPlus, faSmile, faStar } from '@fortawesome/free-solid-svg-icons';
import { CommentService } from 'src/app/comment/comment.service';
import { CommentDetails } from 'src/app/comment/dto/comment-details';
import { UpdatedComment } from 'src/app/comment/dto/updated-comment';
import { Page } from 'src/app/common/dto/page';
import { PostLike } from 'src/app/like/dto/post-like';
import { PostLikeService } from 'src/app/like/post-like.service';
import { PostDetails } from '../dto/post-details';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit {
  @Input('post') post!: PostDetails;
  @Input('minimal') minimal: boolean = false;
  faCheck = faCheckCircle;
  faPlus = faPlus;
  faStar = faStar;
  faSmile = faSmile;
  faAttach = faPaperclip;
  faSend = faPaperPlane;
  comments?: Page<CommentDetails>;
  quickReply: FormGroup;
  message: String = "";
  invalid: boolean = false;

  constructor(private commentService: CommentService, private fb: FormBuilder, private router: Router,
    private likeService: PostLikeService) {
    this.quickReply = this.fb.group({
      content: ['', Validators.required]
    }); 
   }

  ngOnInit(): void {
    this.test();
  }

  reply(): void {
    if(this.quickReply.valid && this.post) {
      this.commentService.newComment({message: this.quickReply.controls['content'].value}, this.post.id)
      .subscribe({
        next: (response: UpdatedComment) => this.refresh(response),
        error: (error: HttpErrorResponse) => this.showError(error)
      });
    }
  }

  showError(error: HttpErrorResponse): void {
    this.message = error.error.message;
    this.invalid = true;
  }

  refresh(response: UpdatedComment): void {
    
  }

  toPost(id: number) {
    this.router.navigate([`post/${id}`])
  }

  plus(vote: boolean) {
    if(vote) {
      this.likePost();
    } else {
      this.unlikePost();
    }
  }

  likePost() {
    this.likeService.likePost({like: true}, this.post.id).subscribe({
      next: (response: PostLike) => this.updateLikes(),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  updateLikes(): void {
    this.post.likeCount += 1; //TODO
  }

  unlikePost() {
    this.likeService.unlikePost(this.post.id).subscribe({
      next: (response: any) => this.updateLikes(),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  test(): void {
    this.comments = {
      content: [{id: 0, content: "comment", edited: false, createdAt: new Date(), user: {username: "Test"} }],
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
