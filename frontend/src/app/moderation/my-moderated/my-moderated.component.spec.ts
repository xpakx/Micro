import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyModeratedComponent } from './my-moderated.component';

describe('MyModeratedComponent', () => {
  let component: MyModeratedComponent;
  let fixture: ComponentFixture<MyModeratedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyModeratedComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyModeratedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
