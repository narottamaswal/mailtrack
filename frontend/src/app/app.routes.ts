import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent) },
  { path: 'create',   loadComponent: () => import('./features/create/create.component').then(m => m.CreateComponent) },
  { path: 'campaigns/:campaignId', loadComponent: () => import('./features/campaign-detail/campaign-detail.component').then(m => m.CampaignDetailComponent) },
  { path: 'campaigns/:campaignId/items/:itemId', loadComponent: () => import('./features/item-detail/item-detail.component').then(m => m.ItemDetailComponent) },
  { path: 'verify/:campaignId/:itemId/:hash',    loadComponent: () => import('./features/public-verify/public-verify.component').then(m => m.PublicVerifyComponent) },
  { path: '**', redirectTo: '' }
];
