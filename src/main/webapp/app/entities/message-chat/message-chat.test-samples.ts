import dayjs from 'dayjs/esm';

import { IMessageChat, NewMessageChat } from './message-chat.model';

export const sampleWithRequiredData: IMessageChat = {
  id: 19767,
  contenu: 'when beside',
  dateEnvoi: dayjs('2026-04-17T18:01'),
};

export const sampleWithPartialData: IMessageChat = {
  id: 19793,
  contenu: 'perspire',
  dateEnvoi: dayjs('2026-04-17T20:20'),
};

export const sampleWithFullData: IMessageChat = {
  id: 9076,
  contenu: 'however',
  dateEnvoi: dayjs('2026-04-18T13:02'),
};

export const sampleWithNewData: NewMessageChat = {
  contenu: 'exalted vivaciously coolly',
  dateEnvoi: dayjs('2026-04-18T00:35'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
