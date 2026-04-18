import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../message-chat.test-samples';

import { MessageChatFormService } from './message-chat-form.service';

describe('MessageChat Form Service', () => {
  let service: MessageChatFormService;

  beforeEach(() => {
    service = TestBed.inject(MessageChatFormService);
  });

  describe('Service methods', () => {
    describe('createMessageChatFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMessageChatFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            contenu: expect.any(Object),
            dateEnvoi: expect.any(Object),
            auteur: expect.any(Object),
            demande: expect.any(Object),
          }),
        );
      });

      it('passing IMessageChat should create a new form with FormGroup', () => {
        const formGroup = service.createMessageChatFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            contenu: expect.any(Object),
            dateEnvoi: expect.any(Object),
            auteur: expect.any(Object),
            demande: expect.any(Object),
          }),
        );
      });
    });

    describe('getMessageChat', () => {
      it('should return NewMessageChat for default MessageChat initial value', () => {
        const formGroup = service.createMessageChatFormGroup(sampleWithNewData);

        const messageChat = service.getMessageChat(formGroup);

        expect(messageChat).toMatchObject(sampleWithNewData);
      });

      it('should return NewMessageChat for empty MessageChat initial value', () => {
        const formGroup = service.createMessageChatFormGroup();

        const messageChat = service.getMessageChat(formGroup);

        expect(messageChat).toMatchObject({});
      });

      it('should return IMessageChat', () => {
        const formGroup = service.createMessageChatFormGroup(sampleWithRequiredData);

        const messageChat = service.getMessageChat(formGroup);

        expect(messageChat).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMessageChat should not enable id FormControl', () => {
        const formGroup = service.createMessageChatFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMessageChat should disable id FormControl', () => {
        const formGroup = service.createMessageChatFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
