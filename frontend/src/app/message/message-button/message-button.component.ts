import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { faEnvelope, IconDefinition } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-message-button',
  templateUrl: './message-button.component.html',
  styleUrls: ['./message-button.component.css']
})
export class MessageButtonComponent implements OnInit {
  icon: IconDefinition = faEnvelope;
  @Output('send') sendEvent = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit(): void {
  }

  send(): void {
    this.sendEvent.emit(true);
  }
}
