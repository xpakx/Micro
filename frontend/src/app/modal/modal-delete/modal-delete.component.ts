import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal-delete',
  templateUrl: './modal-delete.component.html',
  styleUrls: ['./modal-delete.component.css']
})
export class ModalDeleteComponent {
  @Output() closeEvent = new EventEmitter<boolean>();
  @Output() deleteEvent = new EventEmitter<boolean>();
  @Input() text: string = "";

  constructor() { }

  cancel(): void {
    this.closeEvent.emit(true);
  }

  delete(): void {
    this.deleteEvent.emit(true);
  }
}
