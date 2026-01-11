import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ValidatorFn, ValidationErrors, AbstractControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

interface DisplayMessage {
  msgType: string;
  msgBody: string;
}

export function passwordMatchValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const password = group.get('password')?.value;
    const confirm = group.get('confirmPassword')?.value;

    if (!password || !confirm) {
      return null;
    }

    return password === confirm ? null : { passwordsDontMatch: true };
  };
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
      username: ['', Validators.compose([Validators.required, Validators.minLength(8), Validators.maxLength(64)])],
      password: ['', Validators.compose([Validators.required, Validators.minLength(8), Validators.maxLength(32)])],
      confirmPassword: ['', Validators.required],
      firstname: [''],
      lastname: [''],
      address: ['', [Validators.required, Validators.maxLength(256)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(128)]]
    },
    { validators: passwordMatchValidator() }
  );
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.submitted = true;

    const payload = {
      username: this.form.value.username,
      password: this.form.value.password,
      firstname: this.form.value.firstname,
      lastname: this.form.value.lastname,
      address: this.form.value.address,
      email: this.form.value.email
    };

    this.authService.signup(payload).subscribe({
      next: () => {
        this.submitted = false;

        this.router.navigate(['/login', {
          msgType: 'success',
          msgBody: 'Registracija uspešna. Proverite email da biste aktivirali nalog.'
        }], { queryParams: { returnUrl: this.returnUrl } });
      },
      error: (error) => {
        this.submitted = false;
        this.notification = { msgType: 'error', msgBody: error?.error?.message ?? 'Sign up error.' };
      }
    });
  }
  
}
