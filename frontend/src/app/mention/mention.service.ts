import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Page } from '../common/dto/page';
import { MentionCount } from './dto/mention-count';
import { MentionDetails } from './dto/mention-details';
import { MentionRead } from './dto/mention-read';

@Injectable({
  providedIn: 'root'
})
export class MentionService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public getMentionsCount():  Observable<MentionCount> {
    return this.http.get<MentionCount>(`${this.apiServerUrl}/mentions/count`);
  }

  public getMentions(page?: number | undefined):  Observable<Page<MentionDetails>> {
    return this.http.get<Page<MentionDetails>>(`${this.apiServerUrl}/mentions${page ? '?page='+page : ''}`);
  }

  public readMention(request: MentionRead, mentionId: number):  Observable<MentionRead> {
    return this.http.post<MentionRead>(`${this.apiServerUrl}/mentions/${mentionId}/read`, request);
  }

  public readAllMentions(request: MentionRead):  Observable<MentionRead> {
    return this.http.post<MentionRead>(`${this.apiServerUrl}/mentions/read`, request);
  }
}
