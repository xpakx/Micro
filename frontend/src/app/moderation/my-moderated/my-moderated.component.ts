import { Component, OnInit } from '@angular/core';
import { Page } from 'src/app/common/dto/page';
import { ModerationDetails } from '../dto/moderation-details';
import { ModerationService } from '../moderation.service';

@Component({
  selector: 'app-my-moderated',
  templateUrl: './my-moderated.component.html',
  styleUrls: ['./my-moderated.component.css']
})
export class MyModeratedComponent implements OnInit {

  constructor(private modService: ModerationService) { }
  reports: ModerationDetails[] = [];

  ngOnInit(): void {
    this.modService.getMyModerated().subscribe({
      next: (response: Page<ModerationDetails>) => this.onSuccess(response)
    });
  }

  onSuccess(response: Page<ModerationDetails>): void {
    this.reports = response.content;
  }
}
