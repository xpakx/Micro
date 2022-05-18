import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostEditViewComponent } from './post-edit-view.component';

describe('PostEditViewComponent', () => {
  let component: PostEditViewComponent;
  let fixture: ComponentFixture<PostEditViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PostEditViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PostEditViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
