import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MessageDetails } from '../dto/message-details';
import { MessageService } from '../message.service';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageComponent implements OnInit {
  @Input("message") message?: MessageDetails;

  constructor(private messageService: MessageService, private router: Router) { }

  ngOnInit(): void {
  }

  toUser(username: String) {
    this.router.navigate([`user/${username}`]);
  }

}
