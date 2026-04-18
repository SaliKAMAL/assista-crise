import dayjs from 'dayjs/esm';

import { ICrise, NewCrise } from './crise.model';

export const sampleWithRequiredData: ICrise = {
  id: 26287,
  titre: 'quick past',
  type: 'ACCIDENT_INDUSTRIEL',
  dateDebut: dayjs('2026-04-17T17:04'),
  statut: 'FERMEE',
  latitude: -52.18,
  longitude: -168.3,
};

export const sampleWithPartialData: ICrise = {
  id: 19835,
  titre: 'reprove entwine outrank',
  type: 'INONDATION',
  dateDebut: dayjs('2026-04-17T15:39'),
  dateFermeture: dayjs('2026-04-18T07:00'),
  statut: 'ACTIVE',
  latitude: -3.36,
  longitude: 116.83,
};

export const sampleWithFullData: ICrise = {
  id: 6698,
  titre: 'helplessly emphasise',
  description: 'gracefully average opposite',
  type: 'AUTRE',
  dateDebut: dayjs('2026-04-18T13:56'),
  dateFermeture: dayjs('2026-04-18T04:26'),
  statut: 'ACTIVE',
  latitude: -29.84,
  longitude: -11.9,
};

export const sampleWithNewData: NewCrise = {
  titre: 'eulogise contrast',
  type: 'ACCIDENT_INDUSTRIEL',
  dateDebut: dayjs('2026-04-18T04:18'),
  statut: 'FERMEE',
  latitude: 19.57,
  longitude: -129.43,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
