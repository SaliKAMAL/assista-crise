import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IDemande } from 'app/entities/demande/demande.model';
import { DemandeService } from 'app/entities/demande/service/demande.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IMessageChat } from '../message-chat.model';
import { MessageChatService } from '../service/message-chat.service';

import { MessageChatFormGroup, MessageChatFormService } from './message-chat-form.service';

@Component({
  selector: 'jhi-message-chat-update',
  templateUrl: './message-chat-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class MessageChatUpdate implements OnInit {
  readonly isSaving = signal(false);
  messageChat: IMessageChat | null = null;

  usersSharedCollection = signal<IUser[]>([]);
  demandesSharedCollection = signal<IDemande[]>([]);

  protected messageChatService = inject(MessageChatService);
  protected messageChatFormService = inject(MessageChatFormService);
  protected userService = inject(UserService);
  protected demandeService = inject(DemandeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MessageChatFormGroup = this.messageChatFormService.createMessageChatFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareDemande = (o1: IDemande | null, o2: IDemande | null): boolean => this.demandeService.compareDemande(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ messageChat }) => {
      this.messageChat = messageChat;
      if (messageChat) {
        this.updateForm(messageChat);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const messageChat = this.messageChatFormService.getMessageChat(this.editForm);
    if (messageChat.id === null) {
      this.subscribeToSaveResponse(this.messageChatService.create(messageChat));
    } else {
      this.subscribeToSaveResponse(this.messageChatService.update(messageChat));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMessageChat | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(messageChat: IMessageChat): void {
    this.messageChat = messageChat;
    this.messageChatFormService.resetForm(this.editForm, messageChat);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, messageChat.auteur));
    this.demandesSharedCollection.update(demandes =>
      this.demandeService.addDemandeToCollectionIfMissing<IDemande>(demandes, messageChat.demande),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.messageChat?.auteur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));

    this.demandeService
      .query()
      .pipe(map((res: HttpResponse<IDemande[]>) => res.body ?? []))
      .pipe(
        map((demandes: IDemande[]) => this.demandeService.addDemandeToCollectionIfMissing<IDemande>(demandes, this.messageChat?.demande)),
      )
      .subscribe((demandes: IDemande[]) => this.demandesSharedCollection.set(demandes));
  }
}
