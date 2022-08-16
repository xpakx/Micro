import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Page } from '../common/dto/page';
import { Moderation } from './dto/moderation';
import { ModerationDetails } from './dto/moderation-details';
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
  
  public getMyReports(page?: number | undefined): Observable<Page<ModerationDetails>> {
    return this.http.get<Page<ModerationDetails>>(`${this.apiServerUrl}/moderation/my/reports${page ? '?page='+page : ''}`);
  }
  
  public getMyModerated(page?: number | undefined): Observable<Page<ModerationDetails>> {
    return this.http.get<Page<ModerationDetails>>(`${this.apiServerUrl}/moderation/my${page ? '?page='+page : ''}`);
  }

  public getAll(page?: number | undefined): Observable<Page<ModerationDetails>> {
    return this.http.get<Page<ModerationDetails>>(`${this.apiServerUrl}/moderation/all${page ? '?page='+page : ''}`);
  }

  public getUnmoderated(page?: number | undefined): Observable<Page<ModerationDetails>> {
    return this.http.get<Page<ModerationDetails>>(`${this.apiServerUrl}/moderation/unmoderated${page ? '?page='+page : ''}`);
  }
}
