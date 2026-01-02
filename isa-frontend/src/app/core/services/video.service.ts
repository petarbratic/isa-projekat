import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { VideoPost } from '../../features/videos/video.model';

@Injectable({
  providedIn: 'root'
})
export class VideoService {

  private apiUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  getAll(): Observable<VideoPost[]> {
    return this.http.get<VideoPost[]>(`${this.apiUrl}/videos`);
  }

  create(formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUrl}/videos`, formData);
  }

  getThumbnail(videoId: number): string {
    return `${this.apiUrl}/videos/${videoId}/thumbnail`;
  }
}
