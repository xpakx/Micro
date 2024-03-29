import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faCheckCircle, faEdit, faFlag, faPlus, faReply, faTrashAlt } from '@fortawesome/free-solid-svg-icons';
import { CommentLikeService } from 'src/app/like/comment-like.service';
import { CommentLike } from 'src/app/like/dto/comment-like';
import { Unlike } from 'src/app/like/dto/unlike';
import { AuthorizedUserService } from 'src/app/user/authorized-user.service';
import { environment } from 'src/environments/environment';
import { CommentService } from '../comment.service';
import { CommentWithData } from '../dto/comment-with-data';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css']
})
export class CommentComponent implements OnInit {
  @Input('comment') comment!: CommentWithData;
  @Input('postAuthor') postAuthor: boolean = false;
  faCheck = faCheckCircle;
  faPlus = faPlus;
  faReply = faReply;
  faDelete = faTrashAlt;
  faEdit = faEdit;
  faReport = faFlag;
  showDeleteModal: boolean = false;
  private apiServerUrl = environment.apiServerUrl;
  showReportDialog: boolean = false;
  showModDialog: boolean = false;

  constructor(private likeService: CommentLikeService, private commentService: CommentService, private router: Router,
    protected userService: AuthorizedUserService) { }

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
    this.likeService.likeComment({like: true}, this.comment.comment.id).subscribe({
      next: (response: CommentLike) => this.updateLikes(response.totalLikes, true),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  updateLikes(totalLikes: number, like: boolean): void {
    this.comment.comment.likeCount = totalLikes;
    this.comment.liked = like;
  }

  unlikeComment() {
    this.likeService.unlikeComment(this.comment.comment.id).subscribe({
      next: (response: Unlike) => this.updateLikes(response.totalLikes, false),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
  }

  get author(): boolean {
    let username: String | null = localStorage.getItem('username');
    return username != null && (username == this.comment.comment.user.username || this.postAuthor);
  }

  get commentAuthor(): boolean {
    let username: String | null = localStorage.getItem('username');
    return username != null && username == this.comment.comment.user.username;
  }

  askForDelete(): void {
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
  }

  delete(): void {
    this.closeDeleteModal();
    this.commentService.deleteComment(this.comment.comment.id).subscribe({
      next: (response: any, commentId: number = this.comment.comment.id) => this.deleteComment(commentId),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  deleteComment(id: number): void {
    if(this.commentAuthor) {
      this.comment.comment.content = "[comment deleted by author]";
      this.comment.comment.deletedByUser = true;
    } else {
      this.comment.comment.content =  "[comment deleted by post author]";
      this.comment.comment.deletedByPostAuthor = true;
    }
  }

  edit(): void {
    this.router.navigate([`comment/${this.comment.comment.id}/edit`]);
  }

  toUser(username: String) {
    this.router.navigate([`user/${username}`])
  }

  get getAttachmentUrl(): String {
    return this.comment.comment.attachmentUrl ? `${this.apiServerUrl}/attachments/${this.comment.comment.attachmentUrl}` : '';
  }

  switchReportDialog(): void {
    this.showReportDialog = !this.showReportDialog
  }

  closeReportDialog(): void {
    this.showReportDialog = false;
  }

  switchModDialog(): void {
    this.showModDialog = !this.showModDialog;
  }
}
