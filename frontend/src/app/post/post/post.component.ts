import { Component, Input, OnInit } from '@angular/core';
import { faCheckCircle, faPaperclip, faPaperPlane, faPlus, faSmile, faStar } from '@fortawesome/free-solid-svg-icons';
import { CommentDetails } from 'src/app/comment/dto/comment-details';
import { Page } from 'src/app/common/dto/page';
import { PostDetails } from '../dto/post-details';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit {
  @Input('post') post!: PostDetails;
  faCheck = faCheckCircle;
  faPlus = faPlus;
  faStar = faStar;
  faSmile = faSmile;
  faAttach = faPaperclip;
  faSend = faPaperPlane;
  comments?: Page<CommentDetails>;

  constructor() { }

  ngOnInit(): void {
    this.test();
  }

  test(): void {
    this.comments = {
      content: [{id: 0, content: "comment", edited: false, createdAt: new Date(), user: {username: "Test"} }],
      pageable: {
          sort: {
              sorted: true,
              unsorted: true,
              empty: false
          },
          offset: 1,
          pageNumber: 1,
          pageSize: 1,
          paged: true,
          unpaged: true
      },
      totalPages: 1,
      last: true,
      number: 1,
      sort: {
          sorted: true,
          unsorted: true,
          empty: false
      },
      size: 1,
      numberOfElements: 1,
      first: true,
      empty: false
  }
  }
}
