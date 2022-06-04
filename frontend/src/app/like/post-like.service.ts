import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { LikeDetails } from './dto/like-details';
import { LikeRequest } from './dto/like-request';
import { PostLike } from './dto/post-like';
import { Unlike } from './dto/unlike';

@Injectable({
  providedIn: 'root'
})
export class PostLikeService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public likePost(request: LikeRequest, postId: number):  Observable<PostLike> {
    return this.http.post<PostLike>(`${this.apiServerUrl}/posts/${postId}/like`, request);
  }

  public unlikePost(postId: number):  Observable<Unlike> {
    return this.http.delete<Unlike>(`${this.apiServerUrl}/posts/${postId}/like`);
  }

  public getLikeForPost(postId: number):  Observable<LikeDetails> {
    return this.http.get<LikeDetails>(`${this.apiServerUrl}/posts/${postId}/like`);
  }
}
