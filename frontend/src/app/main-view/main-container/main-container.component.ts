import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main-container',
  templateUrl: './main-container.component.html',
  styleUrls: ['./main-container.component.css']
})
export class MainContainerComponent implements OnInit {
  sidebar: number = 0;;

  constructor(private router: Router, private location: Location) { }

  ngOnInit(): void {
    this.router.events.subscribe((val) => {
      this.updateSidebar(this.location.path());
    });
  }

  updateSidebar(url: string) {
    console.log(url);
    if(url.startsWith("/my/")) {
      this.sidebar = 1;
    } else {
      this.sidebar = 0;
    }
  }

}

