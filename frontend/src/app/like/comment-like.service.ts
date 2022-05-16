import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CommentLike } from './dto/comment-like';
import { LikeDetails } from './dto/like-details';
import { LikeRequest } from './dto/like-request';

@Injectable({
  providedIn: 'root'
})
export class CommentLikeService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  private getUsername() {
    return localStorage.getItem("username");
  }

  public likeComment(request: LikeRequest, commentId: number):  Observable<CommentLike> {
    let username = this.getUsername();
    return this.http.post<CommentLike>(`${this.apiServerUrl}/user/${username}/comments/${commentId}/like`, request);
  }

  public unlikeComment(commentId: number):  Observable<any> {
    let username = this.getUsername();
    return this.http.delete<any>(`${this.apiServerUrl}/user/${username}/comments/${commentId}/like`);
  }

  public getLikeForComment(commentId: number):  Observable<LikeDetails> {
    let username = this.getUsername();
    return this.http.get<LikeDetails>(`${this.apiServerUrl}/user/${username}/comments/${commentId}/like`);
  }
}
