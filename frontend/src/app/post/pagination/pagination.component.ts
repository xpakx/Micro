import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent implements OnInit {
  @Output("page") pageEvent = new EventEmitter<number>();
  @Input("pages") totalPages: number = 0;
  currentPage: number = 0;
  @Input("last") lastPage: boolean = true;
  @Input("first") firstPage: boolean = true;
  pages: number[] = [];

  constructor() { }

  
  @Input("curr") set current(page: number) {
    this.currentPage = page;
    this.calculatePages();
  } 

  ngOnInit(): void {
  
  }

  private calculatePages() {
    this.pages = [];
    let min: number = Math.max(0, this.currentPage - 2);
    let max: number = Math.min(this.totalPages, this.currentPage + 3);
    for (let i: number = min; i < max; i++) {
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
