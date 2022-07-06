import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-emoji-choice',
  templateUrl: './emoji-choice.component.html',
  styleUrls: ['./emoji-choice.component.css']
})
export class EmojiChoiceComponent implements OnInit {
  emojiTable: String[] = ["🐱", "😸", "😼"];
  @Output("emoji") emojiEvent: EventEmitter<String> = new EventEmitter<String>();

  constructor() { }

  ngOnInit(): void {
  }

  addEmoji(emoji: String): void {
    this.emojiEvent.emit(emoji);
  }

}
