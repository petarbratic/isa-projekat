import { Injectable } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ApiService } from './api.service';
import { UserService } from './user.service';
import { ConfigService } from './config.service';
import { catchError, map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { of } from 'rxjs/internal/observable/of';
import { Observable } from 'rxjs';

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

  login(user:any) {
    const loginHeaders = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    });
    // const body = `username=${user.username}&password=${user.password}`;
    const body = {
      'email': user.email,
      'password': user.password
    };
    return this.apiService.post(this.config.login_url, JSON.stringify(body), loginHeaders)
      .pipe(map((res) => {
        console.log('Login success');
        this.access_token = res.body.accessToken;
        localStorage.setItem("jwt", res.body.accessToken);
        console.log('Token in localStorage:', localStorage.getItem('jwt'));
        return res.body;
      }));
  }

  signup(user:any) {
    const signupHeaders = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    });
    return this.apiService.post(this.config.signup_url, JSON.stringify(user), signupHeaders)
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
        // ako je userService.currentUser popunjen (posle /whoami), uzmi odatle
        const u: any = this.userService.currentUser;
        if (u?.firstName && u?.lastName) return `${u.firstName} ${u.lastName}`;
        if (u?.username) return u.username;

        // fallback: možeš kasnije čuvati displayName u localStorage,
        // ali sada vraćamo null ako nemamo info
        return null;
    }

}