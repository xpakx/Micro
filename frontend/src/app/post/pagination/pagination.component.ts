import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Pageable } from 'src/app/common/dto/pageable';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent implements OnInit {
  @Output("page") pageEvent = new EventEmitter<number>();
  @Input("pages") totalPages: number = 0;
  @Input("curr") currentPage: number = 0;
  @Input("last") lastPage: boolean = true;
  @Input("first") firstPage: boolean = true;
  pages: number[] = [];

  constructor() { }

  ngOnInit(): void {
    let min: number = Math.max(0, this.currentPage-2);
    let max: number = Math.min(this.totalPages, this.currentPage+3);
    for(let i: number = min; i<max; i++) {
      this.pages.push(i);
    }
  }

  toPage(page: number): void {
    if((this.lastPage && page >= this.currentPage) || (this.firstPage && page <= this.currentPage)) {
      return;
    }
    this.pageEvent.emit(page);
  }
}
