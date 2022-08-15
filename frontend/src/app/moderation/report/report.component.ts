import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ModerationDetails } from '../dto/moderation-details';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {
  @Input("report") report?: ModerationDetails;

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  toPost(id?: number): void {
    if(id) {
      this.router.navigate([`/post/${id}`]);
    }

  }

  toComment(id?: number): void {
    if(id) {
      this.router.navigate([`/comment/${id}`]);
    }
  }
}
