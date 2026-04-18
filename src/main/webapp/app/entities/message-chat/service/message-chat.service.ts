import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMessageChat, NewMessageChat } from '../message-chat.model';

export type PartialUpdateMessageChat = Partial<IMessageChat> & Pick<IMessageChat, 'id'>;

type RestOf<T extends IMessageChat | NewMessageChat> = Omit<T, 'dateEnvoi'> & {
  dateEnvoi?: string | null;
};

export type RestMessageChat = RestOf<IMessageChat>;

export type NewRestMessageChat = RestOf<NewMessageChat>;

export type PartialUpdateRestMessageChat = RestOf<PartialUpdateMessageChat>;

@Injectable()
export class MessageChatsService {
  readonly messageChatsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly messageChatsResource = httpResource<RestMessageChat[]>(() => {
    const params = this.messageChatsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of messageChat that have been fetched. It is updated when the messageChatsResource emits a new value.
   * In case of error while fetching the messageChats, the signal is set to an empty array.
   */
  readonly messageChats = computed(() =>
    (this.messageChatsResource.hasValue() ? this.messageChatsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/message-chats');

  protected convertValueFromServer(restMessageChat: RestMessageChat): IMessageChat {
    return {
      ...restMessageChat,
      dateEnvoi: restMessageChat.dateEnvoi ? dayjs(restMessageChat.dateEnvoi) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class MessageChatService extends MessageChatsService {
  protected readonly http = inject(HttpClient);

  create(messageChat: NewMessageChat): Observable<IMessageChat> {
    const copy = this.convertValueFromClient(messageChat);
    return this.http.post<RestMessageChat>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(messageChat: IMessageChat): Observable<IMessageChat> {
    const copy = this.convertValueFromClient(messageChat);
    return this.http
      .put<RestMessageChat>(`${this.resourceUrl}/${encodeURIComponent(this.getMessageChatIdentifier(messageChat))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(messageChat: PartialUpdateMessageChat): Observable<IMessageChat> {
    const copy = this.convertValueFromClient(messageChat);
    return this.http
      .patch<RestMessageChat>(`${this.resourceUrl}/${encodeURIComponent(this.getMessageChatIdentifier(messageChat))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IMessageChat> {
    return this.http
      .get<RestMessageChat>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IMessageChat[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMessageChat[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getMessageChatIdentifier(messageChat: Pick<IMessageChat, 'id'>): number {
    return messageChat.id;
  }

  compareMessageChat(o1: Pick<IMessageChat, 'id'> | null, o2: Pick<IMessageChat, 'id'> | null): boolean {
    return o1 && o2 ? this.getMessageChatIdentifier(o1) === this.getMessageChatIdentifier(o2) : o1 === o2;
  }

  addMessageChatToCollectionIfMissing<Type extends Pick<IMessageChat, 'id'>>(
    messageChatCollection: Type[],
    ...messageChatsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const messageChats: Type[] = messageChatsToCheck.filter(isPresent);
    if (messageChats.length > 0) {
      const messageChatCollectionIdentifiers = messageChatCollection.map(messageChatItem => this.getMessageChatIdentifier(messageChatItem));
      const messageChatsToAdd = messageChats.filter(messageChatItem => {
        const messageChatIdentifier = this.getMessageChatIdentifier(messageChatItem);
        if (messageChatCollectionIdentifiers.includes(messageChatIdentifier)) {
          return false;
        }
        messageChatCollectionIdentifiers.push(messageChatIdentifier);
        return true;
      });
      return [...messageChatsToAdd, ...messageChatCollection];
    }
    return messageChatCollection;
  }

  protected convertValueFromClient<T extends IMessageChat | NewMessageChat | PartialUpdateMessageChat>(messageChat: T): RestOf<T> {
    return {
      ...messageChat,
      dateEnvoi: messageChat.dateEnvoi?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestMessageChat): IMessageChat {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestMessageChat[]): IMessageChat[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
