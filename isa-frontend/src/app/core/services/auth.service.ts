import { Injectable } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ApiService } from './api.service';
import { UserService } from './user.service';
import { ConfigService } from './config.service';
import { catchError, map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { of } from 'rxjs/internal/observable/of';
import { Observable } from 'rxjs';
import { User } from '../../models/user.model';

export interface LoginRequest {
  email: string;
  password: string;
}

export type SignupRequest = User & { password: string };

export interface AuthResponse {
  accessToken: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private apiService: ApiService,
    private userService: UserService,
    private config: ConfigService,
    private router: Router
  ) {
  }

  private access_token = null;

  login(req: LoginRequest): Observable<AuthResponse> {
    const loginHeaders = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    });
    // const body = `username=${user.username}&password=${user.password}`;
    const body = {
      'email': req.email,
      'password': req.password
    };
    return this.apiService.post(this.config.login_url, JSON.stringify(body), loginHeaders)
    .pipe(map((res) => {
        console.log('Login success');
        this.access_token = res.body.accessToken;
        localStorage.setItem("jwt", res.body.accessToken);
        return res.body as AuthResponse;
    }));
  }

  signup(req: SignupRequest): Observable<void> {
    const signupHeaders = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    });
    return this.apiService.post(this.config.signup_url, JSON.stringify(req), signupHeaders)
    .pipe(map(() => {
        console.log('Sign up success');
    }));
  }

  logout() {
    this.userService.currentUser = null;
    localStorage.removeItem("jwt");
    this.access_token = null;
  }

  tokenIsPresent(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    if (this.access_token) return this.access_token;

    const stored = localStorage.getItem('jwt');
    this.access_token = stored as any; // ili: this.access_token = stored;
    return stored;
}
    getUsername(): string | null {
        const u = this.userService.currentUser as User | null;
        if (!u) return null;

        if (u.firstName && u.lastName) return `${u.firstName} ${u.lastName}`;
        return u.username ?? null;
    }

    activateAccount(token: string): Observable<string> {
        const url = `${this.config.activate_url}?token=${encodeURIComponent(token)}`;
        return this.apiService.getText(url);
    }

}