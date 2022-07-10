import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommentDetails } from '../dto/comment-details';

@Component({
  selector: 'app-comment-search',
  templateUrl: './comment-search.component.html',
  styleUrls: ['./comment-search.component.css']
})
export class CommentSearchComponent implements OnInit {
  @Input('comment') comment!: CommentDetails;

  constructor(private router: Router) { }

  ngOnInit(): void {
  }


  toComment(id: number) {
    this.router.navigate([`post/${id}`])
  }

  toUser(username: String) {
    this.router.navigate([`user/${username}`])
  }
}
