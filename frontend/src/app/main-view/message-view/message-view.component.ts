import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageDetails } from 'src/app/message/dto/message-details';
import { MessageService } from 'src/app/message/message.service';

@Component({
  selector: 'app-message-view',
  templateUrl: './message-view.component.html',
  styleUrls: ['./message-view.component.css']
})
export class MessageViewComponent implements OnInit {
  message?: MessageDetails;

  constructor(private route: ActivatedRoute, private messageService: MessageService) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      this.loadMessage(routeParams['id']);
    });   
  }

  loadMessage(id: number) {
    this.messageService.getMessage(id).subscribe({
      next: (response: MessageDetails) => this.saveMessage(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
    
  }

  saveMessage(response: MessageDetails): void {
    this.message = response;
  }
}
