import {Injectable} from '@angular/core';
import {ApiService} from './api.service';
import {ConfigService} from './config.service';
import {map} from 'rxjs/operators';
import { Observable } from 'rxjs';
import { User } from '../../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  currentUser: User | null = null;

  constructor(
    private apiService: ApiService,
    private config: ConfigService
  ) {
  }

  getMyInfo(): Observable<User> {
    return this.apiService.get(this.config.whoami_url)
        .pipe(map((user: User) => {
        this.currentUser = user;
        return user;
        }));
}

  getAll(): Observable<User[]> {
    return this.apiService.get(this.config.users_url);
  }

}