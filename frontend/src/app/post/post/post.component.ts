import { Component, Input, OnInit } from '@angular/core';
import { PostDetails } from '../dto/post-details';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit {
  @Input('post') post!: PostDetails;

  constructor() { }

  ngOnInit(): void {
  }

}
