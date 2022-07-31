import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { faBell, faEnvelope, faSearch } from '@fortawesome/free-solid-svg-icons';
import { AuthorizedUserService } from './user/authorized-user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'micro2';
  faSearch = faSearch;
  faNotif = faBell;
  faMessage = faEnvelope;
  searchForm: UntypedFormGroup;
  smallWindow: boolean = false;
  showSearch: boolean = false;

  constructor(private router: Router, protected userService: AuthorizedUserService, private fb: UntypedFormBuilder) { 
    this.searchForm = this.fb.group({
      search: ['']
    });
  }

  get logged(): boolean {
    if(localStorage.getItem("token")) {
      return true;
    } else {
      return false;
    }
  }

  ngOnInit(): void {
    if(this.logged) {
      this.userService.logged = true;
      this.userService.startNotifRefresh();
    }
  }

  toMain() {
    this.router.navigate(['']);
  }

  toMyMicro() {
    this.router.navigate(['my/tags']);
  }

  toMentions() {
    this.router.navigate(['my/mentions']);
  }

  toMessages() {
    this.router.navigate(['my/messages']);
  }

  toLogin() {
    this.router.navigate(['login']);
  }

  search(): void {
    if(!this.showSearch) {
      this.showSearch = true;
    } else {
      this.router.navigate(['/search'], { queryParams: {search: this.searchForm.controls['search'].value}});
    }
  }
}
