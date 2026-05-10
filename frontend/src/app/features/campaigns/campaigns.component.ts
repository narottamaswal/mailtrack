import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TrackingService } from '../../core/services/tracking.service';
import { CampaignSummaryResponse } from '../../core/models/tracking.models';

@Component({
  selector: 'app-campaigns',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './campaigns.component.html'
})
export class CampaignsComponent implements OnInit {
  private trackingService = inject(TrackingService);
  campaigns = signal<CampaignSummaryResponse[]>([]);
  loading= signal(true);
  error     = signal<string | null>(null);
  deleting  = signal<string | null>(null);

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true); 
    this.error.set(null);
    this.trackingService.listCampaigns().subscribe({
      next:  d => { 
        this.campaigns.set(d); 
        this.loading.set(false); 
      },
      error: () => { 
        this.error.set('Could not load campaigns.'); 
        this.loading.set(false); 
      }
    });
  }

  delete(id: string) {
    if (!confirm('Delete this campaign and all its items?')) return;
    this.deleting.set(id);
    this.trackingService.deleteCampaign(id).subscribe({
      next:  () => { this.campaigns.update(l => l.filter(c => c.campaignId !== id)); this.deleting.set(null); },
      error: () => { alert('Delete failed.'); this.deleting.set(null); }
    });
  }

  fmt(iso: string) {
    return new Date(iso).toLocaleString('en-IN', { day:'numeric', month:'short', year:'numeric', hour:'2-digit', minute:'2-digit' });
  }
}
