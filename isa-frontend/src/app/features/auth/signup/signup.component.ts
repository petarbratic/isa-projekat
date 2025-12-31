import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

interface DisplayMessage {
  msgType: string;
  msgBody: string;
}

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {
  title = 'Sign up';
  form!: FormGroup;

  /**
   * Boolean used in telling the UI
   * that the form has been submitted
   * and is awaiting a response
   */
  submitted = false;

  /**
   * Notification message from received
   * form request or router
   */
  notification!: DisplayMessage;

  returnUrl!: string;
  private ngUnsubscribe: Subject<void> = new Subject<void>();

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder
  ) {

  }

  ngOnInit() {
    this.route.params
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((params: any) => {
        this.notification = params as DisplayMessage;
      });
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    this.form = this.formBuilder.group({
      username: ['', Validators.compose([Validators.required, Validators.minLength(3), Validators.maxLength(64)])],
      password: ['', Validators.compose([Validators.required, Validators.minLength(3), Validators.maxLength(32)])],
      firstname: [''],
      lastname: [''],
      email: ['']
    });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  onSubmit() {
    this.submitted = true;

    this.authService.signup(this.form.value).subscribe({
      next: () => {
        const creds = {
          username: this.form.value.username,
          password: this.form.value.password
        };

        this.authService.login(creds).subscribe({
          next: () => {
            this.userService.getMyInfo().subscribe({
              next: () => this.router.navigate([this.returnUrl]),
              error: () => this.router.navigate([this.returnUrl]) 
            });
          },
          error: () => {
            this.submitted = false;
            this.notification = { msgType: 'error', msgBody: 'Auto-login nakon registracije nije uspeo.' };
          }
        });
      },
      error: (error) => {
        this.submitted = false;
        this.notification = { msgType: 'error', msgBody: error?.error?.message ?? 'Sign up error.' };
      }
    });
  }
}
