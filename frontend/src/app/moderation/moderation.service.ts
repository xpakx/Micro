import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Moderation } from './dto/moderation';
import { ReportRequest } from './dto/report-request';

@Injectable({
  providedIn: 'root'
})
export class ModerationService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }
  
  public reportPost(request: ReportRequest, postId: number): Observable<Moderation> {
    return this.http.post<Moderation>(`${this.apiServerUrl}/post/${postId}/report`, request);
  }
  
  public reportComment(request: ReportRequest, commentId: number): Observable<Moderation> {
    return this.http.post<Moderation>(`${this.apiServerUrl}/comment/${commentId}/report`, request);
  }
}
