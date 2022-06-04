import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CommentRequest } from './dto/comment-request';
import { UpdatedComment } from './dto/updated-comment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }
  public newComment(request: CommentRequest, postId: number):  Observable<UpdatedComment> {
    return this.http.post<UpdatedComment>(`${this.apiServerUrl}/posts/${postId}/comments`, request);
  }

  public updateComment(request: CommentRequest, id: number):  Observable<UpdatedComment> {
    return this.http.put<UpdatedComment>(`${this.apiServerUrl}/comments/${id}`, request);
  }

  public deleteComment(id: number):  Observable<any> {
    return this.http.delete<any>(`${this.apiServerUrl}/comments/${id}`);
  }
}
