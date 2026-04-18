import dayjs from 'dayjs/esm';

import { ICrise } from 'app/entities/crise/crise.model';
import { IDemande } from 'app/entities/demande/demande.model';
import { IUser } from 'app/entities/user/user.model';

export interface IOffre {
  id: number;
  titre?: string | null;
  description?: string | null;
  dateCreation?: dayjs.Dayjs | null;
  dateMiseAJour?: dayjs.Dayjs | null;
  latitude?: number | null;
  longitude?: number | null;
  archivee?: boolean | null;
  active?: boolean | null;
  citoyen?: Pick<IUser, 'id' | 'login'> | null;
  crise?: Pick<ICrise, 'id' | 'titre'> | null;
  demandes?: Pick<IDemande, 'id' | 'titre'>[] | null;
}

export type NewOffre = Omit<IOffre, 'id'> & { id: null };
