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

  private getUsername() {
    return localStorage.getItem("username");
  }

  public newComment(request: CommentRequest, postId: number):  Observable<UpdatedComment> {
    let username = this.getUsername();
    return this.http.post<UpdatedComment>(`${this.apiServerUrl}/user/${username}/post/${postId}/comments`, request);
  }

  public updateComment(request: CommentRequest, id: number):  Observable<UpdatedComment> {
    let username = this.getUsername();
    return this.http.put<UpdatedComment>(`${this.apiServerUrl}/user/${username}/comments/${id}`, request);
  }

  public deleteComment(id: number):  Observable<any> {
    let username = this.getUsername();
    return this.http.delete<any>(`${this.apiServerUrl}/user/${username}/comments/${id}`);
  }
}
