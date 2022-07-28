import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-attachment-choice',
  templateUrl: './attachment-choice.component.html',
  styleUrls: ['./attachment-choice.component.css']
})
export class AttachmentChoiceComponent implements OnInit {
  @Output("attachment") attachmentEvent = new EventEmitter<String>();

  constructor() { }

  ngOnInit(): void {
  }
}
