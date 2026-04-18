import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import InformationResolve from './route/information-routing-resolve.service';

const informationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/information').then(m => m.Information),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/information-detail').then(m => m.InformationDetail),
    resolve: {
      information: InformationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/information-update').then(m => m.InformationUpdate),
    resolve: {
      information: InformationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/information-update').then(m => m.InformationUpdate),
    resolve: {
      information: InformationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default informationRoute;
