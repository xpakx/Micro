import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { PostRequest } from './dto/post-request';
import { UpdatedPost } from './dto/updated-post';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  private getUsername() {
    return localStorage.getItem("username");
  }

  public newPost(request: PostRequest):  Observable<UpdatedPost> {
    let username = this.getUsername();
    return this.http.post<UpdatedPost>(`${this.apiServerUrl}/user/${username}/post`, request);
  }

  public updatePost(request: PostRequest, id: number):  Observable<UpdatedPost> {
    let username = this.getUsername();
    return this.http.put<UpdatedPost>(`${this.apiServerUrl}/user/${username}/post/${id}`, request);
  }

  public deletePost(id: number):  Observable<any> {
    let username = this.getUsername();
    return this.http.delete<UpdatedPost>(`${this.apiServerUrl}/user/${username}/post/${id}`);
  }
}