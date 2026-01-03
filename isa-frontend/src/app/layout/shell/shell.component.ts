import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-shell',
  templateUrl: './shell.component.html',
  styleUrls: ['./shell.component.scss']
})
export class ShellComponent {
  constructor(
    public authService: AuthService,
    public userService: UserService,
    private router: Router
  ) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['']);
  }
}