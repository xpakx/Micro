import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faMessage, faTags, faUsers } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-my-sidebar',
  templateUrl: './my-sidebar.component.html',
  styleUrls: ['./my-sidebar.component.css']
})
export class MySidebarComponent implements OnInit {
  faTags = faTags;
  faUsers = faUsers;
  faMentions = faMessage;
  faMessages = faMessage;

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  toTags(): void {
    this.router.navigate(["my/tags"]);
  }

  toUsers(): void {
    this.router.navigate(["my/users"]);
  }

  toMentions(): void {
    this.router.navigate(["my/mentions"]);
  }

  toMessages(): void {
    this.router.navigate(["my/messages"]);
  }

}
