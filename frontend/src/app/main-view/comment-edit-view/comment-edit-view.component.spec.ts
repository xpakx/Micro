import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommentEditViewComponent } from './comment-edit-view.component';

describe('CommentEditViewComponent', () => {
  let component: CommentEditViewComponent;
  let fixture: ComponentFixture<CommentEditViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CommentEditViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentEditViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
