import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.css']
})
export class AvatarComponent implements OnInit {
  @Input('url') url: String = 'https://loganfuneralchapel.com/wp-content/uploads/2017/01/xgeneric-profile-avatar_352864.jpg.pagespeed.ic.tKiXzscPKc.jpg';
  color: String = 'gray';
  @Input('gender') set gender(value: String) {
    console.log(value)
    if(value == "male") {
      this.color = 'seagreen';
    } else if(value == "female") {
      this.color = 'pink';
    } else if(value.length < 1) {
      this.color = 'gray';
    } else {
      this.color = 'gold';
    }
    console.log(this.color)
  } 

  constructor() { }

  ngOnInit(): void {
  }

}
