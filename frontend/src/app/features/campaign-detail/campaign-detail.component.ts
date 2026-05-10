import { Component, inject, signal, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TrackingService } from '../../core/services/tracking.service';
import { CampaignDetailResponse, CreateItemResponse, ItemSummaryResponse } from '../../core/models/tracking.models';
import { CopyBtnComponent } from '../../shared/components/copy-button/copy-button.component';
import {AuthService} from "../../core/services/auth.service";

@Component({
  selector: 'app-campaign-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './campaign-detail.component.html'
})
export class CampaignDetailComponent implements OnInit {
  @Input() campaignId!: string;

  private svc = inject(TrackingService);
  protected auth = inject(AuthService);

  campaign    = signal<CampaignDetailResponse | null>(null);
  loading     = signal(true);
  error       = signal<string | null>(null);
  creating    = signal(false);
  newItem     = signal<CreateItemResponse | null>(null);
  deletingItem = signal<string | null>(null);

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true); this.error.set(null);
    this.svc.getCampaign(this.campaignId).subscribe({
      next:  d => { this.campaign.set(d); this.loading.set(false); },
      error: () => { this.error.set('Campaign not found.'); this.loading.set(false); }
    });
  }

  createItem() {
    this.creating.set(true); this.newItem.set(null);
    this.svc.createItem(this.campaignId).subscribe({
      next: res => {
        this.newItem.set(res);
        this.creating.set(false);
        // reload campaign to refresh item list
        this.svc.getCampaign(this.campaignId).subscribe(d => this.campaign.set(d));
      },
      error: () => { alert('Failed to create item.'); this.creating.set(false); }
    });
  }

  deleteItem(itemId: string) {
    if (!confirm(`Delete item ${itemId}?`)) return;
    this.deletingItem.set(itemId);
    this.svc.deleteItem(this.campaignId, itemId).subscribe({
      next: () => {
        this.campaign.update(c => c ? { ...c, items: c.items.filter(i => i.itemId !== itemId) } : c);
        this.deletingItem.set(null);
      },
      error: () => { alert('Delete failed.'); this.deletingItem.set(null); }
    });
  }

  fmt(iso: string) {
    return new Date(iso).toLocaleString('en-IN', { day:'numeric', month:'short', year:'numeric', hour:'2-digit', minute:'2-digit' });
  }

  fmtDur(s: number) {
    if (!s) return '—';
    if (s < 60) return `${Math.round(s)}s`;
    return `${Math.floor(s/60)}m ${Math.round(s%60)}s`;
  }
}
