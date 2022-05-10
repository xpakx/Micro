import { Component, OnInit } from '@angular/core';
import { faPaperclip, faPaperPlane, faSmile } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-post-form',
  templateUrl: './post-form.component.html',
  styleUrls: ['./post-form.component.css']
})
export class PostFormComponent implements OnInit {
  faSmile = faSmile;
  faAttach = faPaperclip;
  faSend = faPaperPlane;

  constructor() { }

  ngOnInit(): void {
  }

}
