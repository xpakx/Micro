import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faCheckCircle, faEdit, faPlus, faReply, faTrashAlt } from '@fortawesome/free-solid-svg-icons';
import { CommentLikeService } from 'src/app/like/comment-like.service';
import { CommentLike } from 'src/app/like/dto/comment-like';
import { Unlike } from 'src/app/like/dto/unlike';
import { CommentService } from '../comment.service';
import { CommentDetails } from '../dto/comment-details';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css']
})
export class CommentComponent implements OnInit {
  @Input('comment') comment!: CommentDetails;
  @Input('postAuthor') postAuthor: boolean = false;
  faCheck = faCheckCircle;
  faPlus = faPlus;
  faReply = faReply;
  faDelete = faTrashAlt;
  faEdit = faEdit;

  constructor(private likeService: CommentLikeService, private commentService: CommentService, private router: Router) { }

  ngOnInit(): void {
  }

  plus(vote: boolean) {
    if(vote) {
      this.likeComment();
    } else {
      this.unlikeComment();
    }
  }

  likeComment() {
    this.likeService.likeComment({like: true}, this.comment.id).subscribe({
      next: (response: CommentLike) => this.updateLikes(response.totalLikes),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  updateLikes(totalLikes: number): void {
    this.comment.likeCount = totalLikes;
  }

  unlikeComment() {
    this.likeService.unlikeComment(this.comment.id).subscribe({
      next: (response: Unlike) => this.updateLikes(response.totalLikes),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
  }

  get author(): boolean {
    let username: String | null = localStorage.getItem('username');
    return username != null && (username == this.comment.user.username || this.postAuthor);
  }

  delete(): void {
    this.commentService.deleteComment(this.comment.id).subscribe({
      next: (response: any) => this.deleteComment(),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  deleteComment(): void {
    //todo
  }

  edit(): void {
    this.router.navigate([`comment/${this.comment.id}/edit`]);
  }
}
