import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModerationFormComponent } from './moderation-form.component';

describe('ModerationFormComponent', () => {
  let component: ModerationFormComponent;
  let fixture: ComponentFixture<ModerationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModerationFormComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModerationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
