import dayjs from 'dayjs/esm';

import { ICrise } from 'app/entities/crise/crise.model';
import { IUser } from 'app/entities/user/user.model';

export interface IInformation {
  id: number;
  titre?: string | null;
  contenu?: string | null;
  datePublication?: dayjs.Dayjs | null;
  latitude?: number | null;
  longitude?: number | null;
  auteur?: Pick<IUser, 'id' | 'login'> | null;
  crise?: Pick<ICrise, 'id' | 'titre'> | null;
}

export type NewInformation = Omit<IInformation, 'id'> & { id: null };
