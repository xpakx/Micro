import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Pageable } from 'src/app/common/dto/pageable';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent implements OnInit {
  @Output("page") pageEvent = new EventEmitter<number>();
  @Input("pageData") pageable?: Pageable;
  pages: number[] = [];

  constructor() { }

  ngOnInit(): void {
    this.pages = [0,1, 2, 3];
  }

  toPage(page: number) {
    this.pageEvent.emit(page);
  }

}
