import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { PostDetails } from './dto/post-details';

@Injectable({
  providedIn: 'root'
})
export class PostListService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public getPosts(page?: number | undefined):  Observable<PostDetails[]> {
    return this.http.get<PostDetails[]>(`${this.apiServerUrl}/posts${page ? '/'+page : ''}`);
  }

  public getUserPosts(username: String, page?: number | undefined):  Observable<PostDetails[]> {
    return this.http.get<PostDetails[]>(`${this.apiServerUrl}/user/${username}/posts${page ? '/'+page : ''}`);
  }
}
