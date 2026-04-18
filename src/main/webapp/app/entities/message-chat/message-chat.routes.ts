import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MessageChatResolve from './route/message-chat-routing-resolve.service';

const messageChatRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/message-chat').then(m => m.MessageChat),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/message-chat-detail').then(m => m.MessageChatDetail),
    resolve: {
      messageChat: MessageChatResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/message-chat-update').then(m => m.MessageChatUpdate),
    resolve: {
      messageChat: MessageChatResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/message-chat-update').then(m => m.MessageChatUpdate),
    resolve: {
      messageChat: MessageChatResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default messageChatRoute;
