import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.css']
})
export class AvatarComponent implements OnInit {
  @Input('url') url: String = 'https://loganfuneralchapel.com/wp-content/uploads/2017/01/xgeneric-profile-avatar_352864.jpg.pagespeed.ic.tKiXzscPKc.jpg';
  @Input('color') color: String = 'pink';

  constructor() { }

  ngOnInit(): void {
  }

}
