import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { CommentDetails } from '../dto/comment-details';

@Component({
  selector: 'app-comment-search',
  templateUrl: './comment-search.component.html',
  styleUrls: ['./comment-search.component.css']
})
export class CommentSearchComponent implements OnInit {
  @Input('comment') comment!: CommentDetails;
  private apiServerUrl = environment.apiServerUrl;

  constructor(private router: Router) { }

  ngOnInit(): void {
  }


  toComment(id: number) {
    this.router.navigate([`post/${id}`])
  }

  toUser(username: String) {
    this.router.navigate([`user/${username}`])
  }

  get getAttachmentUrl(): String {
    return this.comment.attachmentUrl ? `${this.apiServerUrl}/attachments/${this.comment.attachmentUrl}` : '';
  }
}
