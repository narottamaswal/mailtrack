import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators, AbstractControl } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TrackingService } from '../../core/services/tracking.service';
import { CreateCampaignRequest, RedirectLinkConfigRequest } from '../../core/models/tracking.models';
import {AuthService} from "../../core/services/auth.service";

@Component({
  selector: 'app-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './create.component.html'
})
export class CreateComponent {
  private fb     = inject(FormBuilder);
  private svc    = inject(TrackingService);
  private router = inject(Router);
  submitting = signal(false);
  error      = signal<string | null>(null);

  form: FormGroup = this.fb.group({
    name:             [''],
    emailOpenLink:    [false],
    timeTrackingLink: [false],
    redirectLinks:    this.fb.array([])
  });

  get links(): FormArray { return this.form.get('redirectLinks') as FormArray; }
  get linkGroups(): FormGroup[] { return this.links.controls as FormGroup[]; }
  ctrl(g: AbstractControl, n: string) { return (g as FormGroup).get(n); }

  addLink() {
    this.links.push(this.fb.group({
      label:        [''],
      originalUrl:  ['', [Validators.required, Validators.pattern(/^https?:\/\/.+/)]],
      password:     [''],
      viewOnce:     [false],
      noForwarding: [false]
    }));
  }

  removeLink(i: number) { this.links.removeAt(i); }

  hasFeature(): boolean {
    const v = this.form.value;
    return v.emailOpenLink || v.timeTrackingLink || v.redirectLinks?.length > 0;
  }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    if (!this.hasFeature()) { this.error.set('Enable at least one feature or add a redirect link.'); return; }

    const v = this.form.value;
    const req: CreateCampaignRequest = {
      name:             v.name?.trim() || '',
      emailOpenLink:    v.emailOpenLink,
      timeTrackingLink: v.timeTrackingLink,
      redirectLinks: (v.redirectLinks ?? []).map((l: any): RedirectLinkConfigRequest => ({
        label:        l.label?.trim() || 'Link',
        originalUrl:  l.originalUrl,
        password:     l.password?.trim() || null,
        viewOnce:     l.viewOnce,
        noForwarding: l.noForwarding
      }))
    };

    this.submitting.set(true); this.error.set(null);
    this.svc.createCampaign(req).subscribe({
      next:  res => this.router.navigate(['/campaigns', res.campaignId]),
      error: ()  => { this.error.set('Failed to create campaign.'); this.submitting.set(false); }
    });
  }
}
