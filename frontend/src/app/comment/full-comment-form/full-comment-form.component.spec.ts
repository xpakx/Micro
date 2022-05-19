import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FullCommentFormComponent } from './full-comment-form.component';

describe('FullCommentFormComponent', () => {
  let component: FullCommentFormComponent;
  let fixture: ComponentFixture<FullCommentFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FullCommentFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FullCommentFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
