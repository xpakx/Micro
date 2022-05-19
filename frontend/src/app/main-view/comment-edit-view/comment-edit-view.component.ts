import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommentListService } from 'src/app/comment/comment-list.service';
import { CommentDetails } from 'src/app/comment/dto/comment-details';

@Component({
  selector: 'app-comment-edit-view',
  templateUrl: './comment-edit-view.component.html',
  styleUrls: ['./comment-edit-view.component.css']
})
export class CommentEditViewComponent implements OnInit {
  comment?: CommentDetails;

  constructor(private route: ActivatedRoute, private commentService: CommentListService) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      this.loadPost(routeParams['id']);
    });   
  }

  loadPost(id: number) {
    this.commentService.getComment(id).subscribe({
      next: (response: CommentDetails) => this.savePost(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
    
  }

  savePost(response: CommentDetails): void {
    this.comment = response;
  }
}
