import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.css']
})
export class AvatarComponent implements OnInit {
  @Input('url') url: String = '/assets/default-avatar.jpg';
  
  color: String = 'gray';
  @Input('gender') set gender(value: String) {
    if(value == "male") {
      this.color = 'seagreen';
    } else if(value == "female") {
      this.color = 'pink';
    } else if(value.length < 1) {
      this.color = 'gray';
    } else {
      this.color = 'gold';
    }
  } 

  constructor() { }

  ngOnInit(): void {
  }

}
