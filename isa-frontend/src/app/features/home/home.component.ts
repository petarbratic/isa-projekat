import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(
    public authService: AuthService,
    public userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.tokenIsPresent() && !this.userService.currentUser) {
      this.userService.getMyInfo().subscribe();
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('');
  }
}