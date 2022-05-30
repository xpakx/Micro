import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { TagDetails } from './dto/tag-details';

@Injectable({
  providedIn: 'root'
})
export class TagService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public getTopTags():  Observable<TagDetails[]> {
    return this.http.get<TagDetails[]>(`${this.apiServerUrl}/tags/top`);
  }
}
