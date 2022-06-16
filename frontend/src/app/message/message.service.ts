import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Page } from '../common/dto/page';
import { MessageCount } from './dto/message-count';
import { MessageDetails } from './dto/message-details';
import { MessageDto } from './dto/message-dto';
import { MessageMin } from './dto/message-min';
import { MessageRead } from './dto/message-read';
import { MessageRequest } from './dto/message-request';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  sendMessage(request: MessageRequest, recipient: String): Observable<MessageDto> {
    return this.http.post<MessageDto>(`${this.apiServerUrl}/users/${recipient}/messages`, request);
  }

  public getMessagesCount():  Observable<MessageCount> {
    return this.http.get<MessageCount>(`${this.apiServerUrl}/messages/count`);
  }

  public getMessages(page?: number | undefined):  Observable<Page<MessageMin>> {
    return this.http.get<Page<MessageMin>>(`${this.apiServerUrl}/messages${page ? '?page='+page : ''}`);
  }

  public readAllMessages(request: MessageRead):  Observable<MessageRead> {
    return this.http.post<MessageRead>(`${this.apiServerUrl}/messages/read`, request);
  }

  public getMessage(id: number):  Observable<MessageDetails> {
    return this.http.get<MessageDetails>(`${this.apiServerUrl}/messages/${id}`);
  }
}
