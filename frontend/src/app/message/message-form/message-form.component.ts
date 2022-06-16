import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageDto } from '../dto/message-dto';
import { MessageService } from '../message.service';

@Component({
  selector: 'app-message-form',
  templateUrl: './message-form.component.html',
  styleUrls: ['./message-form.component.css']
})
export class MessageFormComponent implements OnInit {
  form: FormGroup;
  public invalid: boolean = false;
  public sent: boolean = false;
  public message: string = '';

  @Input("username") username?: String;

  constructor(private service: MessageService, private fb: FormBuilder) {
    this.form = this.fb.group({
      content: ['', Validators.required]
    });
  }

  ngOnInit(): void {
  }

  sendMessage(): void {
    if(this.form.valid && this.username) {
      this.invalid = false;
      this.service.sendMessage({ content: this.form.controls['content'].value }, this.username).subscribe({
        next: (response: MessageDto) => this.messageSent(response),
        error: (error: HttpErrorResponse) => this.showError(error)
      })
    } else {
      this.message = "Message cannot be empty!";
      this.invalid = true;
    }
  }

  private showError(error: HttpErrorResponse) {
    this.message = error.error.message;
    this.invalid = true;
  }

  private messageSent(response: MessageDto) {
    this.sent = true;
  }

}
