import dayjs from 'dayjs/esm';

import { IInformation, NewInformation } from './information.model';

export const sampleWithRequiredData: IInformation = {
  id: 30844,
  titre: 'ick yawningly border',
  contenu: 'unnecessarily better quaintly',
  datePublication: dayjs('2026-04-17T19:55'),
};

export const sampleWithPartialData: IInformation = {
  id: 15264,
  titre: 'though tall',
  contenu: 'squeaky rout who',
  datePublication: dayjs('2026-04-18T04:53'),
  longitude: -101.86,
};

export const sampleWithFullData: IInformation = {
  id: 23818,
  titre: 'anX',
  contenu: 'miserably instead phooey',
  datePublication: dayjs('2026-04-18T09:07'),
  latitude: 28.85,
  longitude: 73.5,
};

export const sampleWithNewData: NewInformation = {
  titre: 'slowly especially though',
  contenu: 'euphonium',
  datePublication: dayjs('2026-04-17T19:54'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
