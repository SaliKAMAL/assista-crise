import dayjs from 'dayjs/esm';

import { IDemande, NewDemande } from './demande.model';

export const sampleWithRequiredData: IDemande = {
  id: 12976,
  titre: 'tightly',
  description: 'absent',
  statut: 'RESOLUE',
  dateCreation: dayjs('2026-04-17T17:49'),
  archivee: true,
};

export const sampleWithPartialData: IDemande = {
  id: 19725,
  titre: 'overfeed onset too',
  description: 'procrastinate instead',
  statut: 'EN_COURS',
  dateCreation: dayjs('2026-04-17T23:29'),
  dateMiseAJour: dayjs('2026-04-18T04:27'),
  latitude: 10.36,
  longitude: 70.01,
  archivee: true,
};

export const sampleWithFullData: IDemande = {
  id: 5647,
  titre: 'ack adventurously charming',
  description: 'um partridge',
  statut: 'RESOLUE',
  dateCreation: dayjs('2026-04-18T13:28'),
  dateMiseAJour: dayjs('2026-04-18T10:08'),
  latitude: -53.38,
  longitude: -92.09,
  archivee: true,
};

export const sampleWithNewData: NewDemande = {
  titre: 'er impeccable',
  description: 'once yowza finding',
  statut: 'RESOLUE',
  dateCreation: dayjs('2026-04-17T18:05'),
  archivee: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
