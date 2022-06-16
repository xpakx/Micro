import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { MessageCount } from './dto/message-count';
import { MessageDto } from './dto/message-dto';
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
}
