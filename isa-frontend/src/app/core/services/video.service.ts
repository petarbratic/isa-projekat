import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { VideoPost } from '../../features/videos/video.model';
import { catchError, timeout } from 'rxjs/operators';
import { PageResponse, CommentResponse } from 'src/app/models/comment.model';

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
    return this.http.post(`${this.apiUrl}/videos`, formData).pipe(
      timeout(30000), 
      catchError(err => {
        if (err.name === 'TimeoutError') {
          return throwError(() => new Error('Upload je istekao nakon 30 sekundi.'));
        }
        return throwError(() => err);
      })
    );
  }
  
  getVideoUrl(videoId: number): string {
    return `${this.apiUrl}/videos/${videoId}/stream`;
  }

  getThumbnail(videoId: number): string {
    return `${this.apiUrl}/videos/${videoId}/thumbnail`;
  }

  getById(videoId: number): Observable<VideoPost> {
    return this.http.get<VideoPost>(`${this.apiUrl}/videos/${videoId}`);
  }

  getComments(videoId: number, page = 0, size = 10): Observable<PageResponse<CommentResponse>> {
    return this.http.get<PageResponse<CommentResponse>>(
      `${this.apiUrl}/videos/${videoId}/comments?page=${page}&size=${size}`
    );
  }

  addComment(videoId: number, text: string): Observable<CommentResponse> {
    return this.http.post<CommentResponse>(
      `${this.apiUrl}/videos/${videoId}/comments`,
      { text }
    );
  }

  getByUser(userId: number): Observable<VideoPost[]> {
    return this.http.get<VideoPost[]>(`${this.apiUrl}/users/${userId}/videos`);
  }

  toggleLike(videoId: number, shouldLike: boolean): Observable<void> {
    const url = `${this.apiUrl}/videos/${videoId}/likes`;
    return shouldLike
      ? this.http.post<void>(url, {})
      : this.http.delete<void>(url);
  }

}
