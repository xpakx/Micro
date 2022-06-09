import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Page } from '../common/dto/page';
import { PostRequest } from './dto/post-request';
import { PostWithComments } from './dto/post-with-comments';
import { UpdatedPost } from './dto/updated-post';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public newPost(request: PostRequest):  Observable<UpdatedPost> {
    return this.http.post<UpdatedPost>(`${this.apiServerUrl}/posts`, request);
  }

  public updatePost(request: PostRequest, id: number):  Observable<UpdatedPost> {
    return this.http.put<UpdatedPost>(`${this.apiServerUrl}/posts/${id}`, request);
  }

  public deletePost(id: number):  Observable<any> {
    return this.http.delete<UpdatedPost>(`${this.apiServerUrl}/posts/${id}`);
  }

  public getFavPosts(page?: number | undefined):  Observable<Page<PostWithComments>> {
    return this.http.get<Page<PostWithComments>>(`${this.apiServerUrl}/posts/fav${page ? '?page='+page : ''}`);
  }

  public favPost(postId: number):  Observable<any> {
    return this.http.post<any>(`${this.apiServerUrl}/posts/${postId}/fav`, null);
  }

  public unfavPost(postId: number):  Observable<any> {
    return this.http.delete<any>(`${this.apiServerUrl}/posts/${postId}/fav`);
  }

  public getPostsByFollowedUsers(page?: number | undefined):  Observable<Page<PostWithComments>> {
    return this.http.get<Page<PostWithComments>>(`${this.apiServerUrl}/posts/follows/users${page ? '?page='+page : ''}`);
  }

  public getPostsWithFollowedTags(page?: number | undefined):  Observable<Page<PostWithComments>> {
    return this.http.get<Page<PostWithComments>>(`${this.apiServerUrl}/posts/follows/tags${page ? '?page='+page : ''}`);
  }
}
