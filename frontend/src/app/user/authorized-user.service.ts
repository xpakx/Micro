import { Injectable } from '@angular/core';
import { Token } from '../authentication/dto/token';

@Injectable({
  providedIn: 'root'
})
export class AuthorizedUserService {
  logged: boolean = false;

  constructor() { }

  saveData(response: Token) {
    localStorage.setItem("token", response.token);
    localStorage.setItem("username", response.username);
    this.logged = true;
  }

  clearData() {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    this.logged = false;
  }
}
