import { Component, Input, OnInit } from '@angular/core';
import { faCheckCircle, faPaperclip, faPaperPlane, faPlus, faSmile, faStar } from '@fortawesome/free-solid-svg-icons';
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

  constructor() { }

  ngOnInit(): void {
  }

}
