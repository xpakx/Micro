import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { faUser, IconDefinition } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-follow-button',
  templateUrl: './follow-button.component.html',
  styleUrls: ['./follow-button.component.css']
})
export class FollowButtonComponent implements OnInit {
  @Input('followed') followed: boolean = false;
  @Input('icon') icon: IconDefinition = faUser;
  @Output('follow') voteEvent = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit(): void {
  }

  follow(): void {
    this.voteEvent.emit(!this.followed);
  }
}
