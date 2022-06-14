import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Page } from 'src/app/common/dto/page';
import { MentionDetails } from '../dto/mention-details';
import { MentionRead } from '../dto/mention-read';
import { MentionService } from '../mention.service';

@Component({
  selector: 'app-mention-list',
  templateUrl: './mention-list.component.html',
  styleUrls: ['./mention-list.component.css']
})
export class MentionListComponent implements OnInit {
  mentions: MentionDetails[] = [];

  constructor(private mentionService: MentionService, private router: Router) { }

  ngOnInit(): void {
    this.mentionService.getMentions().subscribe({
      next: (response: Page<MentionDetails>) => this.saveMentions(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
    
  }

  saveMentions(response: Page<MentionDetails>): void {
    this.mentions = response.content;
  }

  toMention(mention: MentionDetails) {
    this.router.navigate([`post/${mention.post.id}`]);
    this.mentionService.readMention({read: true}, mention.id).subscribe();
  }

  readAll(): void {
    this.mentionService.readAllMentions({read: true}).subscribe({
      next: (response: MentionRead) => this.markAsRead(),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  markAsRead(): void {
    this.mentions.forEach((m) => m.read = true);
  }
}
