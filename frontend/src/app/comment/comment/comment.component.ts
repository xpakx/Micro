import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { faCheckCircle, faPlus, faReply } from '@fortawesome/free-solid-svg-icons';
import { CommentLikeService } from 'src/app/like/comment-like.service';
import { CommentLike } from 'src/app/like/dto/comment-like';
import { Unlike } from 'src/app/like/dto/unlike';
import { CommentDetails } from '../dto/comment-details';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css']
})
export class CommentComponent implements OnInit {
  @Input('comment') comment!: CommentDetails;
  faCheck = faCheckCircle;
  faPlus = faPlus;
  faReply = faReply;

  constructor(private likeService: CommentLikeService) { }

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
}
