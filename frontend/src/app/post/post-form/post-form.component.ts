import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { faPaperclip, faPaperPlane, faSmile } from '@fortawesome/free-solid-svg-icons';
import { AuthorizedUserService } from 'src/app/user/authorized-user.service';
import { PostDetails } from '../dto/post-details';
import { PostWithComments } from '../dto/post-with-comments';
import { UpdatedPost } from '../dto/updated-post';
import { PostService } from '../post.service';

@Component({
  selector: 'app-post-form',
  templateUrl: './post-form.component.html',
  styleUrls: ['./post-form.component.css']
})
export class PostFormComponent implements OnInit {
  form: UntypedFormGroup;
  message: String = '';
  invalid: boolean = false;
  faSmile = faSmile;
  faAttach = faPaperclip;
  faSend = faPaperPlane;
  @Input("init") init?: String;
  @Output("newPost") newPostEvent: EventEmitter<PostWithComments> = new EventEmitter<PostWithComments>();
  showEmojiDialog: boolean = false;
  showAttachmentDialog: boolean = false;
  @ViewChild("postInput") postElem?: ElementRef;
  attachmentBase64: String = "";

  constructor(private service: PostService, private fb: UntypedFormBuilder, protected userService: AuthorizedUserService) {
    this.form = this.fb.group({
      content: ['', Validators.required]
    }); 
  }

  ngOnInit(): void {
    if(this.init) {
      this.form.setValue({ content: this.init + ' ' });
    }
  }

  sendPost(): void {
    if(this.form.valid) {
      this.service.newPost({message: this.form.controls['content'].value, encodedAttachment: this.attachmentBase64})
      .subscribe({
        next: (response: UpdatedPost) => this.refresh(response),
        error: (error: HttpErrorResponse) => this.showError(error)
      });
    }
  }

  showError(error: HttpErrorResponse): void {
    this.message = error.error.message;
    this.invalid = true;
  }

  refresh(response: UpdatedPost): void {
    let newPost: PostWithComments = {
      post: {
        id: response.id,
        content: response.message,
        createdAt: response.createdAt,
        edited: false,
        likeCount: 0,
        dislikeCount: 0,
        attachmentUrl: response.attachmentUrl,
        user: {
          username: response.username,
          gender: "",
          avatarUrl: "",
          confirmed: false
        }
      }, 
      liked: false, 
      disliked: false, 
      fav: false, 
      comments: {
        content: [], 
        totalPages: 0, 
        totalElements: 0, 
        last: false, 
        number: 0, 
        size: 0, 
        numberOfElements: 0, 
        empty: true, 
        first: true,
        sort: {
          empty: true, 
          sorted: true, 
          unsorted: false
        },
        pageable: {
          sort: {
            empty: true, 
            sorted: true, 
            unsorted: false
          }, 
          offset: 0, 
          pageNumber: 0, 
          pageSize: 0, 
          paged: true, 
          unpaged: false
        }
      }
    };
    
    this.newPostEvent.emit(newPost);
  }

  switchEmojiDialog(): void {
    this.showEmojiDialog = !this.showEmojiDialog;
    this.showAttachmentDialog = false;
  }

  addEmoji(emoji: String): void {
    if(this.postElem) {
      let position: number = this.postElem.nativeElement.selectionStart;
      let oldText: String = this.form.controls['content'].value;
      let newText: String = oldText.slice(0, position) + emoji + oldText.slice(position)
      this.form.controls['content'].setValue(newText);
    }
  }

  switchAttachmentDialog(): void {
    this.showAttachmentDialog = !this.showAttachmentDialog;
    this.showEmojiDialog = false;
  }

  addAttachment(attachment: String): void {
    this.attachmentBase64 = attachment;
  }
}
