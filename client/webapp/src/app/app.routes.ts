import { Routes, RouterModule } from '@angular/router';
import { ModuleWithProviders } from '@angular/core';

import { PoolEcosystemPageComponent } from './swarm/page/pool-ecosystem-page/pool-ecosystem-page.component';
import { DashboardComponent } from './swarm/dashboard/dashboard.component';
import { FaqComponent } from './swarm/faq/faq.component';

export const routes: Routes = [
  { path: '', redirectTo: '/pool/all', pathMatch: 'full' },
  { path: 'pool/:id', component: PoolEcosystemPageComponent },
  { path: 'faq', component: FaqComponent }
];

export const AppRoutes: ModuleWithProviders = RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' });
