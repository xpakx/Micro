import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { faCircleRight, faTrashAlt } from '@fortawesome/free-solid-svg-icons';
import { ModerationDetails } from '../dto/moderation-details';

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

  constructor(private fb: FormBuilder) {this.form = this.fb.nonNullable.group({
    reason: [new String(''), Validators.required]
  }); }

  ngOnInit(): void {

  }

  moderate(toDelete: boolean): void {

  }
}
