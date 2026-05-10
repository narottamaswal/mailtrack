import { Component, inject, signal, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { TrackingService } from '../../core/services/tracking.service';
import { LinkInfoResponse } from '../../core/models/tracking.models';

@Component({
  selector: 'app-public-verify',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './public-verify.component.html'
})
export class PublicVerifyComponent implements OnInit {
  @Input() campaignId!: string;
  @Input() itemId!:     string;
  @Input() hash!:       string;

  private trackingService = inject(TrackingService);
  private formBuilder  = inject(FormBuilder);

  info        = signal<LinkInfoResponse | null>(null);
  infoLoading = signal(true);
  infoError   = signal<string | null>(null);
  verifying   = signal(false);
  verifyError = signal<string | null>(null);
  verified    = signal(false);

  form = this.formBuilder.group({ password: ['', Validators.required] });

  ngOnInit() {
    this.trackingService.getLinkInfo(this.campaignId, this.itemId, this.hash).subscribe({
      next:  d => { this.info.set(d); this.infoLoading.set(false); },
      error: () => { this.infoError.set('Link not found or removed.'); this.infoLoading.set(false); }
    });
  }

  verify() {
    if (this.form.invalid || this.verifying()){
      return;
    }
    this.verifying.set(true);
    this.verifyError.set(null);

    this.trackingService.verifyPassword(this.campaignId, this.itemId, this.hash, { password: this.form.value.password }).subscribe({
      next: res => {
        this.verifying.set(false);
        if (res.valid && res.redirectUrl) {
          this.verified.set(true);
          setTimeout(() => {
              window.location.href = res.redirectUrl!;
            }, 600);
        } else {
          this.verifyError.set('Incorrect password.');
        }
      },
      error: () => {
        this.verifyError.set('Something went wrong.');
        this.verifying.set(false);
      }
    });
  }
}
