import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ChangeGenderRequest } from './dto/change-gender-request';
import { ChangePasswordRequest } from './dto/change-password-request';
import { UserDto } from './dto/user-dto';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public changePassword(request: ChangePasswordRequest):  Observable<UserDto> {
    return this.http.put<UserDto>(`${this.apiServerUrl}/password`, request);
  }

  public changeGender(request: ChangeGenderRequest):  Observable<UserDto> {
    return this.http.put<UserDto>(`${this.apiServerUrl}/gender`, request);
  }

  public sendAvatar(file: File): Observable<any> {  
    let formData: FormData = new FormData();
    formData.append('file', file);
    return this.http.put(`${this.apiServerUrl}/avatar`, formData);
  }
}
