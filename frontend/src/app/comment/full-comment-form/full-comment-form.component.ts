import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { faPaperclip, faPaperPlane, faSmile } from '@fortawesome/free-solid-svg-icons';
import { AuthorizedUserService } from 'src/app/user/authorized-user.service';
import { CommentService } from '../comment.service';
import { CommentDetails } from '../dto/comment-details';
import { UpdatedComment } from '../dto/updated-comment';

@Component({
  selector: 'app-full-comment-form',
  templateUrl: './full-comment-form.component.html',
  styleUrls: ['./full-comment-form.component.css']
})
export class FullCommentFormComponent implements OnInit {
  @Input("editComment") comment?: CommentDetails;
  form: UntypedFormGroup;
  message: String = '';
  invalid: boolean = false;
  faSmile = faSmile;
  faAttach = faPaperclip;
  faSend = faPaperPlane;

  constructor(private service: CommentService, private fb: UntypedFormBuilder, protected userService: AuthorizedUserService) {
    this.form = this.fb.group({
      content: ['', Validators.required]
    }); 
  }

  ngOnInit(): void {
    if(this.comment) {
      this.form.setValue({ content: this.comment.content });
    } 
  }

  editComment(): void {
    if(this.form.valid && this.comment) {
      this.service.updateComment({message: this.form.controls['content'].value, encodedAttachment: ""}, this.comment.id)
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
}
