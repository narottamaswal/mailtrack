import { Component, inject, signal, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TrackingService } from '../../core/services/tracking.service';
import { ItemDetailResponse, RedirectLinkResponse } from '../../core/models/tracking.models';
import { CopyBtnComponent } from '../../shared/components/copy-button/copy-button.component';

@Component({
  selector: 'app-item-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, CopyBtnComponent],
  templateUrl: './item-detail.component.html'
})
export class ItemDetailComponent implements OnInit {
  @Input() campaignId!: string;
  @Input() itemId!:     string;

  private svc = inject(TrackingService);

  item    = signal<ItemDetailResponse | null>(null);
  loading = signal(true);
  error   = signal<string | null>(null);
  openLink = signal<RedirectLinkResponse | null>(null);

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading.set(true);
    this.error.set(null);
    this.svc.getItem(this.campaignId, this.itemId).subscribe({
      next:  d => {
        this.item.set(d);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Item not found.');
        this.loading.set(false);
      }
    });
  }

  toggleLink(link: RedirectLinkResponse) {
    this.openLink.set(this.openLink()?.hash === link.hash ? null : link);
  }

  fmt(iso: string) {
    return new Date(iso).toLocaleString('en-IN', { day:'numeric', month:'short', year:'numeric', hour:'2-digit', minute:'2-digit' });
  }

  fmtDur(s: number | null) {
    if (s === null || s === undefined) return 'ongoing';
    if (s === 0) return '< 1s';
    if (s < 60) return `${s}s`;
    return `${Math.floor(s/60)}m ${s%60}s`;
  }

  ua(str: string): string {
    if (str.includes('iPhone') || str.includes('iPad')) return 'iOS';
    if (str.includes('Android')) return 'Android';
    if (str.includes('Windows')) return 'Windows';
    if (str.includes('Mac')) return 'macOS';
    return str.substring(0, 30);
  }

  avgDur(item: ItemDetailResponse): string {
    const finished = item.sessions.filter(s => s.durationSeconds !== null);
    if (!finished.length) return '—';
    const avg = finished.reduce((a, s) => a + s.durationSeconds!, 0) / finished.length;
    return this.fmtDur(Math.round(avg));
  }
}
