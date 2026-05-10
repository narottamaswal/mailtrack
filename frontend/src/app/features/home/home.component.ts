import { Component, inject, signal, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TrackingService } from '../../core/services/tracking.service';
import { CampaignDetailResponse, CreateItemResponse, ItemSummaryResponse } from '../../core/models/tracking.models';
import { CopyBtnComponent } from '../../shared/components/copy-button/copy-button.component';
import {AuthService} from "../../core/services/auth.service";
import {CampaignDetailComponent} from "../campaign-detail/campaign-detail.component";
import {CampaignsComponent} from "../campaigns/campaigns.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, CampaignsComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  protected auth = inject(AuthService);

  loading     = signal(true);
  error       = signal<string | null>(null);

}
