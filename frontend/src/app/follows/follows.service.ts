import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { FollowRequest } from './dto/follow-request';
import { FollowedResponse } from './dto/followed-response';

@Injectable({
  providedIn: 'root'
})
export class FollowsService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public followUser(request: FollowRequest):  Observable<any> {
    return this.http.post<any>(`${this.apiServerUrl}/follows/users`, request);
  }

  public unfollowUser(name: String):  Observable<any> {
    return this.http.delete<any>(`${this.apiServerUrl}/follows/users/${name}`);
  }

  public followTag(request: FollowRequest):  Observable<any> {
    return this.http.post<any>(`${this.apiServerUrl}/follows/tags`, request);
  }

  public unfollowTag(name: String):  Observable<any> {
    return this.http.delete<any>(`${this.apiServerUrl}/follows/tags/${name}`);
  }

  public isTagFollowed(name: String):  Observable<FollowedResponse> {
    return this.http.get<FollowedResponse>(`${this.apiServerUrl}/tags/${name}/followed`);
  }

  public isUserFollowed(username: String):  Observable<FollowedResponse> {
    return this.http.get<FollowedResponse>(`${this.apiServerUrl}/users/${username}/followed`);
  }
}
