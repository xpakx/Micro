import { Injectable } from '@angular/core';
import { map, Subscription, timer } from 'rxjs';
import { Token } from '../authentication/dto/token';
import { MentionCount } from '../mention/dto/mention-count';
import { MentionService } from '../mention/mention.service';
import { MessageCount } from '../message/dto/message-count';
import { MessageService } from '../message/message.service';
import { AvatarData } from './dto/avatar-data';
import { SettingsService } from './settings.service';

@Injectable({
  providedIn: 'root'
})
export class AuthorizedUserService {
  logged: boolean = false;
  notifSubscription?: Subscription;
  messageCount: number = 0;
  mentionCount: number = 0;
  avatarUrl: String = "";
  gender: String = "";

  constructor(private mentionService: MentionService, private messageService: MessageService, private settingsService: SettingsService) { }

  saveData(response: Token) {
    localStorage.setItem("token", response.token);
    localStorage.setItem("username", response.username);
    this.logged = true;
    this.startNotifRefresh();
  }

  clearData() {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    this.logged = false;
  }

  startNotifRefresh(): void {
    this.notifSubscription = timer(0, 60000).pipe( 
      map(() => { 
        this.loadData(); 
      }) 
    ).subscribe(); 
  }

  loadData(): void {
    if(this.logged) {
      this.mentionService.getMentionsCount().subscribe({
        next: (response: MentionCount) => this.updateMentions(response.count)
      });


      this.messageService.getMessagesCount().subscribe({
        next: (response: MessageCount) => this.updateMessages(response.count)
      });
    }
  }

  updateMessages(count: number): void {
    this.messageCount = count;
  }
  
  updateMentions(count: number): void {
    this.mentionCount = count;
  }

  getAvatarData(): any {
    this.settingsService.getAvatar().subscribe({
      next: (response: AvatarData) => this.updateAvatar(response)
    });
  }
  
  updateAvatar(response: AvatarData): void {
    this.avatarUrl = response.avatarUrl;
    this.gender = response.gender;
  }
}
