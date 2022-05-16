import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { faPlus } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-like-button',
  templateUrl: './like-button.component.html',
  styleUrls: ['./like-button.component.css']
})
export class LikeButtonComponent implements OnInit {
  faPlus = faPlus;
  @Input('count') likes: number = 0;
  @Input('liked') liked: boolean = false;
  @Output('plus') voteEvent = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit(): void {
  }

  plus(): void {
    this.voteEvent.emit(!this.liked);
  }
}
