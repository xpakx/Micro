import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Page } from '../common/dto/page';
import { PostDetails } from './dto/post-details';
import { PostWithComments } from './dto/post-with-comments';

@Injectable({
  providedIn: 'root'
})
export class PostListService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public getPosts(page?: number | undefined):  Observable<Page<PostWithComments>> {
    return this.http.get<Page<PostWithComments>>(`${this.apiServerUrl}/posts${page ? '/'+page : ''}`);
  }

  public getUserPosts(username: String, page?: number | undefined):  Observable<Page<PostWithComments>> {
    return this.http.get<Page<PostWithComments>>(`${this.apiServerUrl}/user/${username}/posts${page ? '/'+page : ''}`);
  }

  public getPostsWithTag(name: String, page?: number | undefined):  Observable<Page<PostWithComments>> {
    return this.http.get<Page<PostWithComments>>(`${this.apiServerUrl}/tags/${name}/posts${page ? '/'+page : ''}`);
  }

  public getPost(id: number):  Observable<PostWithComments> {
    return this.http.get<PostWithComments>(`${this.apiServerUrl}/post/${id}`);
  }

  public getPostMin(id: number):  Observable<PostDetails> {
    return this.http.get<PostDetails>(`${this.apiServerUrl}/post/${id}/min`);
  }

  public getHotPosts(page?: number | undefined):  Observable<Page<PostWithComments>> {
    return this.http.get<Page<PostWithComments>>(`${this.apiServerUrl}/posts/hot${page ? '/'+page : ''}`);
  }

  public getActivePosts(page?: number | undefined):  Observable<Page<PostWithComments>> {
    return this.http.get<Page<PostWithComments>>(`${this.apiServerUrl}/posts/active${page ? '/'+page : ''}`);
  }
}
