import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Page } from 'src/app/common/dto/page';
import { MessageMin } from '../dto/message-min';
import { MessageRead } from '../dto/message-read';
import { MessageService } from '../message.service';

@Component({
  selector: 'app-message-list',
  templateUrl: './message-list.component.html',
  styleUrls: ['./message-list.component.css']
})
export class MessageListComponent implements OnInit {
  messages: MessageMin[] = [];

  constructor(private messageService: MessageService, private router: Router) { }

  ngOnInit(): void {
    this.messageService.getMessages().subscribe({
      next: (response: Page<MessageMin>) => this.saveMentions(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
    
  }

  saveMentions(response: Page<MessageMin>): void {
    this.messages = response.content;
  }

  toMessage(message: MessageMin) {
    this.router.navigate([`message/${message.id}`]);
  }

  readAll(): void {
    this.messageService.readAllMessages({read: true}).subscribe({
      next: (response: MessageRead) => this.markAsRead(),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  markAsRead(): void {
    this.messages.forEach((m) => m.read = true);
  }
}
