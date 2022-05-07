import { Component, Input, OnInit } from '@angular/core';
import { Page } from 'src/app/common/dto/page';
import { PostDetails } from '../dto/post-details';

@Component({
  selector: 'app-general-post-list',
  templateUrl: './general-post-list.component.html',
  styleUrls: ['./general-post-list.component.css']
})
export class GeneralPostListComponent implements OnInit {
  @Input('posts') posts?: Page<PostDetails>;

  constructor() { }

  ngOnInit(): void {
  }
}
