import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpInterceptor,
  HttpEvent
} from '@angular/common/http';
import { AuthService } from '../services/auth.service';

import { Observable} from 'rxjs';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(public auth: AuthService) { }
 intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  const token = localStorage.getItem('jwt'); // direktno iz localStorage
  if (token) {
    console.log('Interceptor adding token:', token);
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  } else {
    console.log('Interceptor no token found');
  }
  return next.handle(request);
}
}