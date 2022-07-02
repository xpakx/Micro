import { Component, Input, OnInit } from '@angular/core';
import { Page } from 'src/app/common/dto/page';
import { PostDetails } from '../dto/post-details';
import { PostWithComments } from '../dto/post-with-comments';

@Component({
  selector: 'app-general-post-list',
  templateUrl: './general-post-list.component.html',
  styleUrls: ['./general-post-list.component.css']
})
export class GeneralPostListComponent implements OnInit {
  @Input('posts') posts?: Page<PostWithComments>;

  constructor() { }

  ngOnInit(): void {
    if(this.posts) {
      for(let post of this.posts.content) {
        post.comments.content.reverse();
      }
    }
  }

  deletePost(id: number) : void {
    if(this.posts) {
      this.posts.content = this.posts.content.filter((p) => p.post.id != id);
    }
  }
}
