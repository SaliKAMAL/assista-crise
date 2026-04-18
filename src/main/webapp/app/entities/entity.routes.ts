import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'assistaCriseApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'crise',
    data: { pageTitle: 'assistaCriseApp.crise.home.title' },
    loadChildren: () => import('./crise/crise.routes'),
  },
  {
    path: 'demande',
    data: { pageTitle: 'assistaCriseApp.demande.home.title' },
    loadChildren: () => import('./demande/demande.routes'),
  },
  {
    path: 'offre',
    data: { pageTitle: 'assistaCriseApp.offre.home.title' },
    loadChildren: () => import('./offre/offre.routes'),
  },
  {
    path: 'information',
    data: { pageTitle: 'assistaCriseApp.information.home.title' },
    loadChildren: () => import('./information/information.routes'),
  },
  {
    path: 'message-chat',
    data: { pageTitle: 'assistaCriseApp.messageChat.home.title' },
    loadChildren: () => import('./message-chat/message-chat.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
