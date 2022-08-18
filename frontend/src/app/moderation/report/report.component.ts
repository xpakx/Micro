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
  @Input("moderation") mod = false;

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  toPost(id?: number): void {
    if(id) {
      this.router.navigate([`/post/${id}`]);
    }

  }

  toComment(postId?: number, commentId?: number): void {
    if(postId && commentId) {
      this.router.navigate([`/post/${postId}#${commentId}`]);
    }
  }
}
