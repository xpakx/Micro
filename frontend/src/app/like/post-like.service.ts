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

  private getUsername() {
    return localStorage.getItem("username");
  }

  public likePost(request: LikeRequest, postId: number):  Observable<PostLike> {
    let username = this.getUsername();
    return this.http.post<PostLike>(`${this.apiServerUrl}/user/${username}/posts/${postId}/like`, request);
  }

  public unlikePost(postId: number):  Observable<Unlike> {
    let username = this.getUsername();
    return this.http.delete<Unlike>(`${this.apiServerUrl}/user/${username}/posts/${postId}/like`);
  }

  public getLikeForPost(postId: number):  Observable<LikeDetails> {
    let username = this.getUsername();
    return this.http.get<LikeDetails>(`${this.apiServerUrl}/user/${username}/posts/${postId}/like`);
  }
}
