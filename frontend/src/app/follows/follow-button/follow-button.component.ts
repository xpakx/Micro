import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { faUser } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-follow-button',
  templateUrl: './follow-button.component.html',
  styleUrls: ['./follow-button.component.css']
})
export class FollowButtonComponent implements OnInit {
  faFollow = faUser;
  @Input('followed') followed: boolean = false;
  @Output('plus') voteEvent = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit(): void {
  }

  follow(): void {
    this.voteEvent.emit(!this.followed);
  }
}
