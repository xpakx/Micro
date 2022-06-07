import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { faBell, faSearch } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'micro2';
  faSearch = faSearch;
  faNotif = faBell;

  constructor(private router: Router) { }

  toMain() {
    this.router.navigate([''])
  }
}
