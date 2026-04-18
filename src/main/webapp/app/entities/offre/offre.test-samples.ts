import dayjs from 'dayjs/esm';

import { IOffre, NewOffre } from './offre.model';

export const sampleWithRequiredData: IOffre = {
  id: 11043,
  titre: 'ick tune abscond',
  description: 'off generally instead',
  dateCreation: dayjs('2026-04-17T18:03'),
  archivee: true,
  active: false,
};

export const sampleWithPartialData: IOffre = {
  id: 11516,
  titre: 'homeschool sediment loyally',
  description: 'briskly smog',
  dateCreation: dayjs('2026-04-18T07:35'),
  archivee: false,
  active: false,
};

export const sampleWithFullData: IOffre = {
  id: 14500,
  titre: 'redevelop how boohoo',
  description: 'grandson hospitable',
  dateCreation: dayjs('2026-04-17T22:40'),
  dateMiseAJour: dayjs('2026-04-17T17:57'),
  latitude: -72.91,
  longitude: -134.77,
  archivee: false,
  active: true,
};

export const sampleWithNewData: NewOffre = {
  titre: 'that',
  description: 'beside',
  dateCreation: dayjs('2026-04-18T05:28'),
  archivee: false,
  active: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
