import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-activate',
  templateUrl: './activate.component.html',
  styleUrls: ['./activate.component.scss']
})
export class ActivateComponent implements OnInit, OnDestroy {

  loading = true;
  msgType: 'success' | 'error' = 'success';
  msgBody = 'Aktivacija u toku...';

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(params => {
        const token = params.get('token');

        if (!token) {
          this.loading = false;
          this.msgType = 'error';
          this.msgBody = 'Nedostaje token za aktivaciju.';
          return;
        }

        this.authService.activateAccount(token).subscribe({
          next: () => {
            this.loading = false;
            this.msgType = 'success';
            this.msgBody = 'Nalog je uspešno aktiviran. Možete da se ulogujete.';
          },
          error: (err) => {
            this.loading = false;
            this.msgType = 'error';

            const msg =
              typeof err?.error === 'string'
                ? err.error
                : (err?.error?.message ?? 'Greška pri aktivaciji naloga.');

            this.msgBody = msg;
          }
        });
      });
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
