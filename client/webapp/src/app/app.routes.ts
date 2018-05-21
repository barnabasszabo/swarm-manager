import {Routes, RouterModule} from '@angular/router';
import {ModuleWithProviders} from '@angular/core';

import { PoolEcosystemPageComponent } from './swarm/page/pool-ecosystem-page/pool-ecosystem-page.component';
import { DashboardComponent } from './swarm/dashboard/dashboard.component';

export const routes: Routes = [
    {path: '', component: DashboardComponent},
    {path: 'pool/:id', component: PoolEcosystemPageComponent}
];

export const AppRoutes: ModuleWithProviders = RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'});
