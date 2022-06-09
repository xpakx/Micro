import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FollowTagViewComponent } from './follow-tag-view.component';

describe('FollowTagViewComponent', () => {
  let component: FollowTagViewComponent;
  let fixture: ComponentFixture<FollowTagViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FollowTagViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FollowTagViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
