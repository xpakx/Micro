import { Component, OnInit } from '@angular/core';
import { AuthorizedUserService } from 'src/app/user/authorized-user.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

  constructor(private loginService: AuthorizedUserService) { }

  ngOnInit(): void {
    this.loginService.clearData();
  }
}
