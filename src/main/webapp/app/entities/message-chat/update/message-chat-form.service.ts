import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMessageChat, NewMessageChat } from '../message-chat.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMessageChat for edit and NewMessageChatFormGroupInput for create.
 */
type MessageChatFormGroupInput = IMessageChat | PartialWithRequiredKeyOf<NewMessageChat>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMessageChat | NewMessageChat> = Omit<T, 'dateEnvoi'> & {
  dateEnvoi?: string | null;
};

type MessageChatFormRawValue = FormValueOf<IMessageChat>;

type NewMessageChatFormRawValue = FormValueOf<NewMessageChat>;

type MessageChatFormDefaults = Pick<NewMessageChat, 'id' | 'dateEnvoi'>;

type MessageChatFormGroupContent = {
  id: FormControl<MessageChatFormRawValue['id'] | NewMessageChat['id']>;
  contenu: FormControl<MessageChatFormRawValue['contenu']>;
  dateEnvoi: FormControl<MessageChatFormRawValue['dateEnvoi']>;
  auteur: FormControl<MessageChatFormRawValue['auteur']>;
  demande: FormControl<MessageChatFormRawValue['demande']>;
};

export type MessageChatFormGroup = FormGroup<MessageChatFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MessageChatFormService {
  createMessageChatFormGroup(messageChat?: MessageChatFormGroupInput): MessageChatFormGroup {
    const messageChatRawValue = this.convertMessageChatToMessageChatRawValue({
      ...this.getFormDefaults(),
      ...(messageChat ?? { id: null }),
    });
    return new FormGroup<MessageChatFormGroupContent>({
      id: new FormControl(
        { value: messageChatRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      contenu: new FormControl(messageChatRawValue.contenu, {
        validators: [Validators.required, Validators.minLength(1), Validators.maxLength(1000)],
      }),
      dateEnvoi: new FormControl(messageChatRawValue.dateEnvoi, {
        validators: [Validators.required],
      }),
      auteur: new FormControl(messageChatRawValue.auteur, {
        validators: [Validators.required],
      }),
      demande: new FormControl(messageChatRawValue.demande, {
        validators: [Validators.required],
      }),
    });
  }

  getMessageChat(form: MessageChatFormGroup): IMessageChat | NewMessageChat {
    return this.convertMessageChatRawValueToMessageChat(form.getRawValue() as MessageChatFormRawValue | NewMessageChatFormRawValue);
  }

  resetForm(form: MessageChatFormGroup, messageChat: MessageChatFormGroupInput): void {
    const messageChatRawValue = this.convertMessageChatToMessageChatRawValue({ ...this.getFormDefaults(), ...messageChat });
    form.reset({
      ...messageChatRawValue,
      id: { value: messageChatRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MessageChatFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateEnvoi: currentTime,
    };
  }

  private convertMessageChatRawValueToMessageChat(
    rawMessageChat: MessageChatFormRawValue | NewMessageChatFormRawValue,
  ): IMessageChat | NewMessageChat {
    return {
      ...rawMessageChat,
      dateEnvoi: dayjs(rawMessageChat.dateEnvoi, DATE_TIME_FORMAT),
    };
  }

  private convertMessageChatToMessageChatRawValue(
    messageChat: IMessageChat | (Partial<NewMessageChat> & MessageChatFormDefaults),
  ): MessageChatFormRawValue | PartialWithRequiredKeyOf<NewMessageChatFormRawValue> {
    return {
      ...messageChat,
      dateEnvoi: messageChat.dateEnvoi ? messageChat.dateEnvoi.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
