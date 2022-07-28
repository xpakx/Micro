import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { faPaperclip, faPaperPlane, faSmile } from '@fortawesome/free-solid-svg-icons';
import { PostDetails } from '../dto/post-details';
import { UpdatedPost } from '../dto/updated-post';
import { PostService } from '../post.service';

@Component({
  selector: 'app-full-post-form',
  templateUrl: './full-post-form.component.html',
  styleUrls: ['./full-post-form.component.css']
})
export class FullPostFormComponent implements OnInit {
  @Input("editPost") post?: PostDetails;
  form: UntypedFormGroup;
  message: String = '';
  invalid: boolean = false;
  faSmile = faSmile;
  faAttach = faPaperclip;
  faSend = faPaperPlane;
  attachmentBase64: String = "";


  constructor(private service: PostService, private fb: UntypedFormBuilder) {
    this.form = this.fb.group({
      content: ['', Validators.required]
    }); 
  }

  ngOnInit(): void {
    if(this.post) {
      this.form.setValue({ content: this.post.content });
    } 
  }

  buttonAction(): void {
    if(this.post) { this.editPost(); }
    else { this.sendPost(); }
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

  editPost(): void {
    if(this.form.valid && this.post) {
      this.service.updatePost({message: this.form.controls['content'].value, encodedAttachment: this.attachmentBase64}, this.post.id)
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
    
  }

  addAttachment(attachment: String): void {
    this.attachmentBase64 = attachment;
  }
}
