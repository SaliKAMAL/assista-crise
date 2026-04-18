import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import OffreResolve from './route/offre-routing-resolve.service';

const offreRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/offre').then(m => m.Offre),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/offre-detail').then(m => m.OffreDetail),
    resolve: {
      offre: OffreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/offre-update').then(m => m.OffreUpdate),
    resolve: {
      offre: OffreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/offre-update').then(m => m.OffreUpdate),
    resolve: {
      offre: OffreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default offreRoute;
