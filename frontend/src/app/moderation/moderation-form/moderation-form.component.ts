import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { faCircleRight, faTrashAlt } from '@fortawesome/free-solid-svg-icons';
import { Moderation } from '../dto/moderation';
import { ModerationDetails } from '../dto/moderation-details';
import { ModerationService } from '../moderation.service';

export interface ModerationForm {
  reason: FormControl<String>;
}

@Component({
  selector: 'app-moderation-form',
  templateUrl: './moderation-form.component.html',
  styleUrls: ['./moderation-form.component.css']
})
export class ModerationFormComponent implements OnInit {
  @Input("report") report?: ModerationDetails;
  form: FormGroup<ModerationForm>;
  faReject = faCircleRight;
  faDelete = faTrashAlt;

  constructor(private fb: FormBuilder, private modService: ModerationService) {
    this.form = this.fb.nonNullable.group({
      reason: [new String(''), Validators.required]
    }); 
}

  ngOnInit(): void {
    if(this.report) {
      this.form.setValue({ reason: this.report.reason });
    } 
  }

  moderate(toDelete: boolean): void {
    if(this.report) {
      this.modService.moderate({reason: this.form.controls.reason.value, delete: toDelete}, this.report.id).subscribe({
        next: (response: Moderation) => this.onSuccess(response)
      });
    }
  }

  onSuccess(response: Moderation): void {
    //TODO
  }
}
