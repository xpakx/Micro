import { Component, Input, OnInit } from '@angular/core';
import { faCheckCircle, faPlus, faReply } from '@fortawesome/free-solid-svg-icons';
import { CommentDetails } from '../dto/comment-details';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css']
})
export class CommentComponent implements OnInit {
  @Input('comment') comment!: CommentDetails;
  faCheck = faCheckCircle;
  faPlus = faPlus;
  faReply = faReply;

  constructor() { }

  ngOnInit(): void {
  }
}
