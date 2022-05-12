import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralCommentListComponent } from './general-comment-list.component';

describe('GeneralCommentListComponent', () => {
  let component: GeneralCommentListComponent;
  let fixture: ComponentFixture<GeneralCommentListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeneralCommentListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneralCommentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
