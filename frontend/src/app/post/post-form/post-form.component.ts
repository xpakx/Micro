import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { faPaperclip, faPaperPlane, faSmile } from '@fortawesome/free-solid-svg-icons';
import { UpdatedPost } from '../dto/updated-post';
import { PostService } from '../post.service';

@Component({
  selector: 'app-post-form',
  templateUrl: './post-form.component.html',
  styleUrls: ['./post-form.component.css']
})
export class PostFormComponent implements OnInit {
  form: FormGroup;
  message: String = '';
  invalid: boolean = false;
  faSmile = faSmile;
  faAttach = faPaperclip;
  faSend = faPaperPlane;

  constructor(private service: PostService, private fb: FormBuilder) {
    this.form = this.fb.group({
    content: ['', Validators.required]
  }); }

  ngOnInit(): void {
  }

  sendPost(): void {
    if(this.form.valid) {
      this.service.newPost({message: this.form.controls['content'].value})
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
}
