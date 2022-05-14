import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Page } from '../common/dto/page';
import { PostDetails } from './dto/post-details';

@Injectable({
  providedIn: 'root'
})
export class PostListService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public getPosts(page?: number | undefined):  Observable<Page<PostDetails>> {
    return this.http.get<Page<PostDetails>>(`${this.apiServerUrl}/posts${page ? '/'+page : ''}`);
  }

  public getUserPosts(username: String, page?: number | undefined):  Observable<Page<PostDetails>> {
    return this.http.get<Page<PostDetails>>(`${this.apiServerUrl}/user/${username}/posts${page ? '/'+page : ''}`);
  }

  public getPost(id: number):  Observable<PostDetails> {
    return this.http.get<PostDetails>(`${this.apiServerUrl}/post/${id}`);
  }
}
