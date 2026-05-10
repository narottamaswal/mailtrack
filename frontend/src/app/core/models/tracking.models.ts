// ── Request Models ───────────────────────────────

export interface RedirectLinkConfigRequest {
  label: string;
  originalUrl: string;
  password: string | null;
  viewOnce: boolean;
  noForwarding: boolean;
}
export interface User {
  id: number;
  email: string;
  name: string;
  picture: string;
  avatar: string;
}
export interface CreateCampaignRequest {
  name: string;
  emailOpenLink: boolean;
  timeTrackingLink: boolean;
  redirectLinks: RedirectLinkConfigRequest[];
}

export interface VerifyPasswordRequest {
  password: string;
}

// ── Response Models ──────────────────────────────

export interface RedirectLinkConfigResponse {
  id: number;
  label: string;
  originalUrl: string;
  isPasswordProtected: boolean;
  viewOnce: boolean;
  noForwarding: boolean;
}

export interface CampaignSummaryResponse {
  campaignId: string;
  name: string | null;
  createdAt: string;
  emailOpenEnabled: boolean;
  timeTrackerEnabled: boolean;
  redirectLinksCount: number;
  itemCount: number;
}

export interface ItemSummaryResponse {
  itemId: string;
  campaignId: string;
  createdAt: string;
  openCount: number;
  sessionCount: number;
  avgDurationSeconds: number;
  redirectLinkCount: number;
}

export interface CampaignDetailResponse {
  campaignId: string;
  name: string | null;
  createdAt: string;
  emailOpenEnabled: boolean;
  timeTrackerEnabled: boolean;
  redirectLinkConfigs: RedirectLinkConfigResponse[];
  items: ItemSummaryResponse[];
}

export interface RedirectLinkSimpleResponse {
  hash: string;
  label: string;
  shortUrl: string;
  isPasswordProtected: boolean;
  viewOnce: boolean;
  noForwarding: boolean;
}

export interface CreateItemResponse {
  itemId: string;
  campaignId: string;
  createdAt: string;
  emailOpenUrl: string | null;
  emailPixelSnippet: string | null;
  timeTrackerUrl: string | null;
  redirectLinks: RedirectLinkSimpleResponse[];
}

export interface OpenEventResponse {
  timestamp: string;
  ip: string;
  userAgent: string;
}

export interface TrackerSessionResponse {
  sessionId: string;
  startedAt: string;
  durationSeconds: number | null;
  ip: string;
}

export interface ClickEventResponse {
  timestamp: string;
  ip: string;
  userAgent: string;
}

export interface RedirectLinkResponse {
  hash: string;
  label: string;
  shortUrl: string;
  originalUrl: string;
  isPasswordProtected: boolean;
  viewOnce: boolean;
  isExpired: boolean;
  noForwarding: boolean;
  clickCount: number;
  clickEvents: ClickEventResponse[];
}

export interface ItemDetailResponse {
  itemId: string;
  campaignId: string;
  createdAt: string;
  emailOpenEnabled: boolean;
  emailOpenUrl: string | null;
  emailPixelSnippet: string | null;
  openCount: number;
  openEvents: OpenEventResponse[];
  timeTrackerEnabled: boolean;
  timeTrackerUrl: string | null;
  sessionCount: number;
  avgDurationSeconds: number;
  sessions: TrackerSessionResponse[];
  redirectLinks: RedirectLinkResponse[];
}

export interface LinkInfoResponse {
  label: string;
  isPasswordProtected: boolean;
  isExpired: boolean;
  isViewOnce: boolean;
}

export interface VerifyPasswordResponse {
  valid: boolean;
  redirectUrl: string | null;
}
