import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralPostListComponent } from './general-post-list.component';

describe('GeneralPostListComponent', () => {
  let component: GeneralPostListComponent;
  let fixture: ComponentFixture<GeneralPostListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeneralPostListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneralPostListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
