import { Component, OnInit } from '@angular/core';
import { Page } from 'src/app/common/dto/page';
import { ModerationDetails } from '../dto/moderation-details';
import { ModerationService } from '../moderation.service';

@Component({
  selector: 'app-my-reports',
  templateUrl: './my-reports.component.html',
  styleUrls: ['./my-reports.component.css']
})
export class MyReportsComponent implements OnInit {
  reports: ModerationDetails[] = [];

  constructor(private modService: ModerationService) { }

  ngOnInit(): void {
    this.modService.getMyReports().subscribe({
      next: (response: Page<ModerationDetails>) => this.onSuccess(response)
    });
  }

  onSuccess(response: Page<ModerationDetails>): void {
    this.reports = response.content;
  }

}
