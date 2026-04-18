import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDemande } from 'app/entities/demande/demande.model';
import { DemandeService } from 'app/entities/demande/service/demande.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IMessageChat } from '../message-chat.model';
import { MessageChatService } from '../service/message-chat.service';

import { MessageChatFormService } from './message-chat-form.service';
import { MessageChatUpdate } from './message-chat-update';

describe('MessageChat Management Update Component', () => {
  let comp: MessageChatUpdate;
  let fixture: ComponentFixture<MessageChatUpdate>;
  let activatedRoute: ActivatedRoute;
  let messageChatFormService: MessageChatFormService;
  let messageChatService: MessageChatService;
  let userService: UserService;
  let demandeService: DemandeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(MessageChatUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    messageChatFormService = TestBed.inject(MessageChatFormService);
    messageChatService = TestBed.inject(MessageChatService);
    userService = TestBed.inject(UserService);
    demandeService = TestBed.inject(DemandeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const messageChat: IMessageChat = { id: 25461 };
      const auteur: IUser = { id: 3944 };
      messageChat.auteur = auteur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [auteur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ messageChat });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Demande query and add missing value', () => {
      const messageChat: IMessageChat = { id: 25461 };
      const demande: IDemande = { id: 27574 };
      messageChat.demande = demande;

      const demandeCollection: IDemande[] = [{ id: 27574 }];
      vitest.spyOn(demandeService, 'query').mockReturnValue(of(new HttpResponse({ body: demandeCollection })));
      const additionalDemandes = [demande];
      const expectedCollection: IDemande[] = [...additionalDemandes, ...demandeCollection];
      vitest.spyOn(demandeService, 'addDemandeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ messageChat });
      comp.ngOnInit();

      expect(demandeService.query).toHaveBeenCalled();
      expect(demandeService.addDemandeToCollectionIfMissing).toHaveBeenCalledWith(
        demandeCollection,
        ...additionalDemandes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.demandesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const messageChat: IMessageChat = { id: 25461 };
      const auteur: IUser = { id: 3944 };
      messageChat.auteur = auteur;
      const demande: IDemande = { id: 27574 };
      messageChat.demande = demande;

      activatedRoute.data = of({ messageChat });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(auteur);
      expect(comp.demandesSharedCollection()).toContainEqual(demande);
      expect(comp.messageChat).toEqual(messageChat);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMessageChat>();
      const messageChat = { id: 6277 };
      vitest.spyOn(messageChatFormService, 'getMessageChat').mockReturnValue(messageChat);
      vitest.spyOn(messageChatService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ messageChat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(messageChat);
      saveSubject.complete();

      // THEN
      expect(messageChatFormService.getMessageChat).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(messageChatService.update).toHaveBeenCalledWith(expect.objectContaining(messageChat));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMessageChat>();
      const messageChat = { id: 6277 };
      vitest.spyOn(messageChatFormService, 'getMessageChat').mockReturnValue({ id: null });
      vitest.spyOn(messageChatService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ messageChat: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(messageChat);
      saveSubject.complete();

      // THEN
      expect(messageChatFormService.getMessageChat).toHaveBeenCalled();
      expect(messageChatService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMessageChat>();
      const messageChat = { id: 6277 };
      vitest.spyOn(messageChatService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ messageChat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(messageChatService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareDemande', () => {
      it('should forward to demandeService', () => {
        const entity = { id: 27574 };
        const entity2 = { id: 24127 };
        vitest.spyOn(demandeService, 'compareDemande');
        comp.compareDemande(entity, entity2);
        expect(demandeService.compareDemande).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
