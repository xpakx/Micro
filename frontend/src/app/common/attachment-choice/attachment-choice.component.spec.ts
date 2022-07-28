import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttachmentChoiceComponent } from './attachment-choice.component';

describe('AttachmentChoiceComponent', () => {
  let component: AttachmentChoiceComponent;
  let fixture: ComponentFixture<AttachmentChoiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AttachmentChoiceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AttachmentChoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
