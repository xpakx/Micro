import { Component, Input, OnInit } from '@angular/core';
import { Page } from 'src/app/common/dto/page';
import { CommentWithData } from '../dto/comment-with-data';

@Component({
  selector: 'app-general-comment-list',
  templateUrl: './general-comment-list.component.html',
  styleUrls: ['./general-comment-list.component.css']
})
export class GeneralCommentListComponent implements OnInit {
  @Input('comments') comments?: Page<CommentWithData>;
  @Input('postAuthor') postAuthor: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

}
