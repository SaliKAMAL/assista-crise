import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CriseResolve from './route/crise-routing-resolve.service';

const criseRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/crise').then(m => m.Crise),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/crise-detail').then(m => m.CriseDetail),
    resolve: {
      crise: CriseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/crise-update').then(m => m.CriseUpdate),
    resolve: {
      crise: CriseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/crise-update').then(m => m.CriseUpdate),
    resolve: {
      crise: CriseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default criseRoute;
