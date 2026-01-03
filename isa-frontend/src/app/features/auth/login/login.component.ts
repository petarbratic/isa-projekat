import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { UserService } from 'src/app/core/services/user.service';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';


interface DisplayMessage {
  msgType: string;
  msgBody: string;
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  title = 'Login';
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
  ) { }

  ngOnInit() {
    this.route.params
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((params: any) => {
        this.notification = params as DisplayMessage;
      });
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    this.form = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email, Validators.maxLength(128)]],
      password: ['', Validators.compose([Validators.required, Validators.minLength(3), Validators.maxLength(32)])]
    });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  onSubmit() {

  this.submitted = true;
  this.notification ;
  this.authService.login(this.form.value).subscribe({
    next: (res) => {
      console.log('Logged in, token:', this.authService.getToken());
      
      // odmah dobijemo info o korisniku
      this.userService.getMyInfo().subscribe({
        next: (user) => console.log('User info:', user),
        error: (err) => console.error('Failed to fetch user info:', err)
      });

      this.router.navigate([this.returnUrl]);
    },
    error: (err) => {
      console.error('Login failed:', err);
      this.submitted = false;
      this.notification = { msgType: 'error', msgBody: 'Incorrect email or password.' };
    }
  });
}

    if (this.form.invalid) return;

    this.notification = undefined as any;
    this.submitted = true;

    this.authService.login(this.form.value).subscribe({
      next: (data) => {
        console.log(data);
        this.userService.getMyInfo().subscribe();
        this.submitted = false;
        this.router.navigate([this.returnUrl]);
      },
      error: (error) => {
        console.log(error);
        this.submitted = false;

        const msg =
          typeof error?.error === 'string'
            ? error.error
            : (error?.error?.message ?? 'Login error.');

        this.notification = { msgType: 'error', msgBody: msg };
      }
    });
  }


}