import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CommentLike } from './dto/comment-like';
import { LikeDetails } from './dto/like-details';
import { LikeRequest } from './dto/like-request';
import { Unlike } from './dto/unlike';

@Injectable({
  providedIn: 'root'
})
export class CommentLikeService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public likeComment(request: LikeRequest, commentId: number):  Observable<CommentLike> {
    return this.http.post<CommentLike>(`${this.apiServerUrl}/comments/${commentId}/like`, request);
  }

  public unlikeComment(commentId: number):  Observable<Unlike> {
    return this.http.delete<Unlike>(`${this.apiServerUrl}/comments/${commentId}/like`);
  }

  public getLikeForComment(commentId: number):  Observable<LikeDetails> {
    return this.http.get<LikeDetails>(`${this.apiServerUrl}/comments/${commentId}/like`);
  }
}
