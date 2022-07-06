import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmojiChoiceComponent } from './emoji-choice.component';

describe('EmojiChoiceComponent', () => {
  let component: EmojiChoiceComponent;
  let fixture: ComponentFixture<EmojiChoiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmojiChoiceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmojiChoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
