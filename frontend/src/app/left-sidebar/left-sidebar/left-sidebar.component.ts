import { Component, OnInit } from '@angular/core';
import { faFire, faHeart, faNewspaper } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-left-sidebar',
  templateUrl: './left-sidebar.component.html',
  styleUrls: ['./left-sidebar.component.css']
})
export class LeftSidebarComponent implements OnInit {
  faNew = faNewspaper;
  faHot = faFire;
  faFav = faHeart;

  constructor() { }

  ngOnInit(): void {
  }

}
