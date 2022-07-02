import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { faCheckCircle, faEdit, faPaperclip, faPaperPlane, faPlus, faSmile, faStar, faTrashAlt } from '@fortawesome/free-solid-svg-icons';
import { CommentListService } from 'src/app/comment/comment-list.service';
import { CommentService } from 'src/app/comment/comment.service';
import { CommentWithData } from 'src/app/comment/dto/comment-with-data';
import { UpdatedComment } from 'src/app/comment/dto/updated-comment';
import { Page } from 'src/app/common/dto/page';
import { PostLike } from 'src/app/like/dto/post-like';
import { Unlike } from 'src/app/like/dto/unlike';
import { PostLikeService } from 'src/app/like/post-like.service';
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
  quickReply: FormGroup;
  message: String = "";
  invalid: boolean = false;
  showDeleteModal: boolean = false;

  constructor(private commentService: CommentService, private fb: FormBuilder, private router: Router,
    private likeService: PostLikeService, private postService: PostService, private commentListService: CommentListService) {
    this.quickReply = this.fb.group({
      content: ['', Validators.required]
    }); 
   }

  ngOnInit(): void {
  }

  reply(): void {
    if(this.quickReply.valid && this.post) {
      this.commentService.newComment({message: this.quickReply.controls['content'].value}, this.post.id)
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
        likeCount: 0,
        dislikeCount: 0,
        user: {
          username: response.username
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
}
