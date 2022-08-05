import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Moderation } from '../dto/moderation';
import { ModerationService } from '../moderation.service';

export interface ReportForm {
  reason: FormControl<String>;
}

@Component({
  selector: 'app-report-form',
  templateUrl: './report-form.component.html',
  styleUrls: ['./report-form.component.css']
})
export class ReportFormComponent implements OnInit {
  @Input('post') reportPost: boolean = true;
  @Input('id') id?: number;
  @Output('close') closeEvent = new EventEmitter<boolean>();
  form: FormGroup<ReportForm>;

  constructor(private moderationService: ModerationService, private fb: FormBuilder) { 
    this.form = this.fb.nonNullable.group({
      reason: [new String(''), Validators.required]
    });
  }

  ngOnInit(): void {
  }

  send() {
    if(this.id && this.reportPost) {
      this.sendForPost(this.id);
    } else if(this.id) {
      this.sendForComment(this.id);
    }
  }
  
  sendForPost(id: number) {
    this.moderationService.reportPost({reason: this.form.controls.reason.value}, id).subscribe({
      next: (response: Moderation) => this.onSuccess(response),
      error: (error: HttpErrorResponse) => this.onError(error)
    })
  }

  sendForComment(id: number) {
    this.moderationService.reportComment({reason: this.form.controls.reason.value}, id).subscribe({
      next: (response: Moderation) => this.onSuccess(response),
      error: (error: HttpErrorResponse) => this.onError(error)
    })
  }

  onSuccess(response: Moderation): void {
    this.closeEvent.emit(true);
  }

  onError(error: HttpErrorResponse): void {
    //TODO
  }

}
