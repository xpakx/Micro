import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faBell, faEnvelope, faSearch } from '@fortawesome/free-solid-svg-icons';
import { MentionCount } from './mention/dto/mention-count';
import { MentionService } from './mention/mention.service';
import { MessageCount } from './message/dto/message-count';
import { MessageService } from './message/message.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'micro2';
  faSearch = faSearch;
  faNotif = faBell;
  faMessage = faEnvelope;
  mentionsCount: number = 0;
  messagesCount: number = 0;

  constructor(private router: Router, private mentionService: MentionService, private messageService: MessageService) { }

  ngOnInit(): void {
    if(localStorage.getItem("token")) {
      this.mentionService.getMentionsCount().subscribe({
        next: (response: MentionCount) => this.updateMentions(response.count)
      });


      this.messageService.getMessagesCount().subscribe({
        next: (response: MessageCount) => this.updateMessages(response.count)
      });
    }
  }

  updateMentions(count: number): void {
    this.mentionsCount = count;
  }

  updateMessages(count: number): void {
    this.messagesCount = count;
  }

  toMain() {
    this.router.navigate(['']);
  }

  toMentions() {
    this.router.navigate(['my/mentions']);
  }

  toMessages() {
    this.router.navigate(['my/messages']);
  }
}
