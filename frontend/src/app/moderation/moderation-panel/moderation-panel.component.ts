import { Component, OnInit } from '@angular/core';
import { Page } from 'src/app/common/dto/page';
import { ModerationDetails } from '../dto/moderation-details';
import { ModerationService } from '../moderation.service';

@Component({
  selector: 'app-moderation-panel',
  templateUrl: './moderation-panel.component.html',
  styleUrls: ['./moderation-panel.component.css']
})
export class ModerationPanelComponent implements OnInit {
  reports?: Page<ModerationDetails>;
  onlyUnmoderated: boolean = true;
  
  constructor(private modService: ModerationService) { }
  
  ngOnInit(): void {
    this.getUnmoderated();
  }

  private getUnmoderated(page?: number | undefined) {
    this.modService.getUnmoderated().subscribe({
      next: (response: Page<ModerationDetails>) => this.onSuccess(response, true)
    });
  }

  private getAll(page?: number | undefined) {
    this.modService.getAll().subscribe({
      next: (response: Page<ModerationDetails>) => this.onSuccess(response, true)
    });
  }

  onSuccess(response: Page<ModerationDetails>, onlyUnmoderated: boolean): void {
    this.reports = response;
    this.onlyUnmoderated = onlyUnmoderated;
  }

  switchUnmoderated(): void {
    this.onlyUnmoderated = !this.onlyUnmoderated;
    if(this.onlyUnmoderated) {
      this.getUnmoderated();
    } else {
      this.getAll();
    }
  }

  toPage(page: number): void {
    if(this.onlyUnmoderated) {
      this.getUnmoderated(page);
    } else {
      this.getAll(page);
    }
  }
}
