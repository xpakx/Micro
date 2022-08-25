import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { SearchCommentDetails } from '../dto/search-comment-details';

@Component({
  selector: 'app-comment-search',
  templateUrl: './comment-search.component.html',
  styleUrls: ['./comment-search.component.css']
})
export class CommentSearchComponent implements OnInit {
  @Input('comment') comment!: SearchCommentDetails;
  private apiServerUrl = environment.apiServerUrl;

  constructor(private router: Router) { }

  ngOnInit(): void {
  }


  toComment(postId: number, commentId: number) {
    this.router.navigate([`post/${postId}#${commentId}`])
  }

  toUser(username: String) {
    this.router.navigate([`user/${username}`])
  }

  get getAttachmentUrl(): String {
    return this.comment.attachmentUrl ? `${this.apiServerUrl}/attachments/${this.comment.attachmentUrl}` : '';
  }
}
