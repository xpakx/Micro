import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FullPostFormComponent } from './full-post-form.component';

describe('FullPostFormComponent', () => {
  let component: FullPostFormComponent;
  let fixture: ComponentFixture<FullPostFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FullPostFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FullPostFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
