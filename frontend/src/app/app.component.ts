import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faBell, faSearch } from '@fortawesome/free-solid-svg-icons';
import { MentionCount } from './mention/dto/mention-count';
import { MentionService } from './mention/mention.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'micro2';
  faSearch = faSearch;
  faNotif = faBell;
  mentionsCount: number = 0;

  constructor(private router: Router, private mentionService: MentionService) { }

  ngOnInit(): void {
    if(localStorage.getItem("token")) {
      this.mentionService.getMentionsCount().subscribe({
        next: (response: MentionCount) => this.updateMentions(response.count)
      });
    }
  }

  updateMentions(count: number): void {
    this.mentionsCount = count;
  }

  toMain() {
    this.router.navigate(['']);
  }

  toMentions() {
    this.router.navigate(['my/mentions']);
  }
}
