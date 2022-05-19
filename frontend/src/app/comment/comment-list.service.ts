import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Page } from '../common/dto/page';
import { CommentDetails } from './dto/comment-details';

@Injectable({
  providedIn: 'root'
})
export class CommentListService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public getComments(postId: number, page?: number | undefined):  Observable<Page<CommentDetails>> {
    return this.http.get<Page<CommentDetails>>(`${this.apiServerUrl}/posts/${postId}/comments${page ? '/'+page : ''}`);
  }

  public getComment(commentId: number): Observable<CommentDetails> {
    return this.http.get<CommentDetails>(`${this.apiServerUrl}/comments/${commentId}`);
  }
}
