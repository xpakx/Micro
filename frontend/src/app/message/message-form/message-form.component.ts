import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { faPaperPlane } from '@fortawesome/free-solid-svg-icons';
import { MessageDto } from '../dto/message-dto';
import { MessageService } from '../message.service';

@Component({
  selector: 'app-message-form',
  templateUrl: './message-form.component.html',
  styleUrls: ['./message-form.component.css']
})
export class MessageFormComponent implements OnInit {
  form: UntypedFormGroup;
  public invalid: boolean = false;
  public sent: boolean = false;
  public message: string = '';
  faSend = faPaperPlane;

  @Input("username") username?: String;

  constructor(private service: MessageService, private fb: UntypedFormBuilder) {
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
