import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CreateCampaignRequest, CampaignSummaryResponse, CampaignDetailResponse,
  CreateItemResponse, ItemDetailResponse,
  VerifyPasswordRequest, VerifyPasswordResponse, LinkInfoResponse
} from '../models/tracking.models';

@Injectable({ providedIn: 'root' })
export class TrackingService {
  private http = inject(HttpClient);
  private api  = '/api';

  // ── Campaigns ─────────────────────────────────
  createCampaign(req: CreateCampaignRequest): Observable<CampaignDetailResponse> {
    return this.http.post<CampaignDetailResponse>(`${this.api}/campaigns`, req);
  }
  listCampaigns(): Observable<CampaignSummaryResponse[]> {
    return this.http.get<CampaignSummaryResponse[]>(`${this.api}/campaigns`);
  }
  getCampaign(campaignId: string): Observable<CampaignDetailResponse> {
    return this.http.get<CampaignDetailResponse>(`${this.api}/campaigns/${campaignId}`);
  }
  deleteCampaign(campaignId: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/campaigns/${campaignId}`);
  }

  // ── Items ─────────────────────────────────────
  createItem(campaignId: string): Observable<CreateItemResponse> {
    return this.http.post<CreateItemResponse>(`${this.api}/campaigns/${campaignId}/items`, {});
  }
  getItem(campaignId: string, itemId: string): Observable<ItemDetailResponse> {
    return this.http.get<ItemDetailResponse>(`${this.api}/campaigns/${campaignId}/items/${itemId}`);
  }
  deleteItem(campaignId: string, itemId: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/campaigns/${campaignId}/items/${itemId}`);
  }

  // ── Redirect Link (public) ────────────────────
  getLinkInfo(campaignId: string, itemId: string, hash: string): Observable<LinkInfoResponse> {
    return this.http.get<LinkInfoResponse>(`${this.api}/campaigns/${campaignId}/items/${itemId}/r/${hash}/info`);
  }

  verifyPassword(campaignId: string, itemId: string, hash: string, req: {
    password: string | null | undefined
  }): Observable<VerifyPasswordResponse> {
    return this.http.post<VerifyPasswordResponse>(`${this.api}/campaigns/${campaignId}/items/${itemId}/r/${hash}/verify`, req);
  }
}
