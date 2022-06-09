import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FollowUserViewComponent } from './follow-user-view.component';

describe('FollowUserViewComponent', () => {
  let component: FollowUserViewComponent;
  let fixture: ComponentFixture<FollowUserViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FollowUserViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FollowUserViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
