import { Component, Input, OnInit } from '@angular/core';
import { Page } from 'src/app/common/dto/page';
import { CommentDetails } from '../dto/comment-details';

@Component({
  selector: 'app-general-comment-list',
  templateUrl: './general-comment-list.component.html',
  styleUrls: ['./general-comment-list.component.css']
})
export class GeneralCommentListComponent implements OnInit {
  @Input('comments') comments?: Page<CommentDetails>;

  constructor() { }

  ngOnInit(): void {
  }

}
