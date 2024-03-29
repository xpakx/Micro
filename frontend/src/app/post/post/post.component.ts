import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { faCheckCircle, faEdit, faFlag, faPaperclip, faPaperPlane, faPlus, faSmile, faStar, faTrashAlt } from '@fortawesome/free-solid-svg-icons';
import { CommentListService } from 'src/app/comment/comment-list.service';
import { CommentService } from 'src/app/comment/comment.service';
import { CommentWithData } from 'src/app/comment/dto/comment-with-data';
import { UpdatedComment } from 'src/app/comment/dto/updated-comment';
import { Page } from 'src/app/common/dto/page';
import { PostLike } from 'src/app/like/dto/post-like';
import { Unlike } from 'src/app/like/dto/unlike';
import { PostLikeService } from 'src/app/like/post-like.service';
import { AuthorizedUserService } from 'src/app/user/authorized-user.service';
import { environment } from 'src/environments/environment';
import { PostDetails } from '../dto/post-details';
import { PostService } from '../post.service';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit {
  @Input('post') post!: PostDetails;
  @Input('minimal') minimal: boolean = false;
  @Input('sidebar') sidebar: boolean = false;
  @Input('commentsPage') comments?: Page<CommentWithData>;
  @Input('like') like: boolean = false;
  @Input('dislike') dislike: boolean = false;
  @Input('fav') inFav: boolean = false;
  @Output('deletion') deletionEvent: EventEmitter<number> = new EventEmitter<number>();
  faCheck = faCheckCircle;
  faPlus = faPlus;
  faStar = faStar;
  faSmile = faSmile;
  faAttach = faPaperclip;
  faSend = faPaperPlane;
  faDelete = faTrashAlt;
  faEdit = faEdit;
  faReport = faFlag;
  quickReply: UntypedFormGroup;
  message: String = "";
  invalid: boolean = false;
  showDeleteModal: boolean = false;
  showEmojiDialog: boolean = false;
  showReportDialog: boolean = false;
  showModDialog: boolean = false;
  @ViewChild("responseInput") responseElem?: ElementRef;
  showAttachmentDialog: boolean = false;
  attachmentBase64: String = "";


  private apiServerUrl = environment.apiServerUrl;

  constructor(private commentService: CommentService, private fb: UntypedFormBuilder, private router: Router,
    private likeService: PostLikeService, private postService: PostService, private commentListService: CommentListService, 
    protected userService: AuthorizedUserService) {
    this.quickReply = this.fb.group({
      content: ['', Validators.required]
    }); 
   }

  ngOnInit(): void {
  }

  reply(): void {
    if(this.quickReply.valid && this.post) {
      this.commentService.newComment({message: this.quickReply.controls['content'].value, encodedAttachment: this.attachmentBase64}, this.post.id)
      .subscribe({
        next: (response: UpdatedComment) => this.addNewComment(response),
        error: (error: HttpErrorResponse) => this.showError(error)
      });
    }
  }

  showError(error: HttpErrorResponse): void {
    this.message = error.error.message;
    this.invalid = true;
  }

  addNewComment(response: UpdatedComment): void {
    let newComment: CommentWithData = {
      liked: false, 
      disliked: false, 
      comment: {
        id: response.id, 
        content: response.message, 
        createdAt: response.createdAt,
        edited: false,
        deletedByUser: false,
        deletedByPostAuthor: false,
        deletedByModerator: false,
        likeCount: 0,
        dislikeCount: 0,
        attachmentUrl: response.attachmentUrl,
        user: {
          username: response.username,
          gender: this.userService.gender,
          avatarUrl: this.userService.avatarUrl
        }
      }
    }
    this.comments?.content.push(newComment);
  }

  toPost(id: number) {
    this.router.navigate([`post/${id}`])
  }

  toUser(username: String) {
    this.router.navigate([`user/${username}`])
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
      next: (response: PostLike) => this.updateLikes(response.totalLikes, true),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  updateLikes(totalLikes: number, like: boolean): void {
    this.post.likeCount = totalLikes;
    this.like = like;
  }

  unlikePost() {
    this.likeService.unlikePost(this.post.id).subscribe({
      next: (response: Unlike) => this.updateLikes(response.totalLikes, false),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  get author(): boolean {
    let username: String | null = localStorage.getItem('username');
    return username != null && username == this.post.user.username;
  }
  
  askForDelete(): void {
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
  }

  delete(): void {
    this.closeDeleteModal();
    this.postService.deletePost(this.post.id).subscribe({
      next: (response: any) => this.deletePost(),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  deletePost(): void {
    this.deletionEvent.emit(this.post.id);
  }

  edit(): void {
    this.router.navigate([`post/${this.post.id}/edit`]);
  }

  get moreComments(): String {
    if(!this.comments) {
      return "More";
    }
    if(this.comments.totalElements > 2) {
      return 'Read all ' + this.comments.totalElements + ' comments';
    }
    return "Go to post";
  }

  favPost() {
    this.postService.favPost(this.post.id).subscribe({
      next: (response: any) => this.updateFav(true),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  updateFav(fav: boolean): void {
    this.inFav = fav;
  }

  unfavPost() {
    this.postService.unfavPost(this.post.id).subscribe({
      next: (response: any) => this.updateFav(false),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  fav() {
    if(this.inFav) {
      this.unfavPost();
    } else {
      this.favPost();
    }
  }

  nextPage(): void {
    if(this.comments && !this.comments.last) {
      this.commentListService.getComments(this.post.id, this.comments.number+1).subscribe({
        next: (response: Page<CommentWithData>) => this.updateComments(response),
        error: (error: HttpErrorResponse) => this.showError(error)
      });
    }
  }

  updateComments(response: Page<CommentWithData>): void {
    if(this.comments) {
      let newComments: CommentWithData[] = response.content;
      newComments.reverse();
      this.comments.content = newComments.concat(this.comments.content);
      this.comments.last = response.last;
      this.comments.totalPages = response.totalPages;
      this.comments.totalElements = response.totalElements;
      this.comments.number = response.number;
      this.comments.numberOfElements += response.numberOfElements;
    }
  }

  switchEmojiDialog(): void {
    this.showEmojiDialog = !this.showEmojiDialog;
    this.showAttachmentDialog = false;
  }

  addEmoji(emoji: String): void {
    if(this.responseElem) {
      let position: number = this.responseElem.nativeElement.selectionStart;
      let oldText: String = this.quickReply.controls['content'].value;
      let newText: String = oldText.slice(0, position) + emoji + oldText.slice(position)
      this.quickReply.controls['content'].setValue(newText);
    }
  }

  get getAttachmentUrl(): String {
    return this.post.attachmentUrl ? `${this.apiServerUrl}/attachments/${this.post.attachmentUrl}` : '';
  }

  addAttachment(attachment: String): void {
    this.attachmentBase64 = attachment;
  }

  switchAttachmentDialog(): void {
    this.showAttachmentDialog = !this.showAttachmentDialog;
    this.showEmojiDialog = false;
  }

  switchReportDialog(): void {
    this.showReportDialog = !this.showReportDialog;
  }

  closeReportDialog(): void {
    this.showReportDialog = false;
  }

  switchModDialog(): void {
    this.showModDialog = !this.showModDialog;
  }
}
