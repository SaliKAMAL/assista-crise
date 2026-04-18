import dayjs from 'dayjs/esm';

import { StatutCrise } from 'app/entities/enumerations/statut-crise.model';
import { TypeCrise } from 'app/entities/enumerations/type-crise.model';
import { IUser } from 'app/entities/user/user.model';

export interface ICrise {
  id: number;
  titre?: string | null;
  description?: string | null;
  type?: keyof typeof TypeCrise | null;
  dateDebut?: dayjs.Dayjs | null;
  dateFermeture?: dayjs.Dayjs | null;
  statut?: keyof typeof StatutCrise | null;
  latitude?: number | null;
  longitude?: number | null;
  declarant?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewCrise = Omit<ICrise, 'id'> & { id: null };
