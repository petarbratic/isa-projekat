import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { UserService } from 'src/app/core/services/user.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

interface DisplayMessage {
  msgType: string;
  msgBody: string;
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  title = 'Login';
  form!: FormGroup;

  submitted = false;
  notification?: DisplayMessage;
  returnUrl!: string;

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.route.params
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((params: any) => {
        this.notification = params as DisplayMessage;
      });

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';

    this.form = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email, Validators.maxLength(128)]],
      password: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(32)]]
    });
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.notification = undefined;
    this.submitted = true;

    this.authService.login(this.form.value).subscribe({
      next: () => {
        console.log('Logged in, token:', this.authService.getToken());

        // odmah dobijemo info o korisniku
        this.userService.getMyInfo().subscribe({
          next: (user) => console.log('User info:', user),
          error: (err) => console.error('Failed to fetch user info:', err)
        });

        this.submitted = false;
        this.router.navigate([this.returnUrl]);
      },
      error: (err) => {
        console.error('Login failed:', err);
        this.submitted = false;

        const msg =
          typeof err?.error === 'string'
            ? err.error
            : (err?.error?.message ?? 'Incorrect email or password.');

        this.notification = { msgType: 'error', msgBody: msg };
      }
    });
  }
}
