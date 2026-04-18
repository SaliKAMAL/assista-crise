import dayjs from 'dayjs/esm';

import { ICrise } from 'app/entities/crise/crise.model';
import { StatutDemande } from 'app/entities/enumerations/statut-demande.model';
import { IOffre } from 'app/entities/offre/offre.model';
import { IUser } from 'app/entities/user/user.model';

export interface IDemande {
  id: number;
  titre?: string | null;
  description?: string | null;
  statut?: keyof typeof StatutDemande | null;
  dateCreation?: dayjs.Dayjs | null;
  dateMiseAJour?: dayjs.Dayjs | null;
  latitude?: number | null;
  longitude?: number | null;
  archivee?: boolean | null;
  sinistre?: Pick<IUser, 'id' | 'login'> | null;
  crise?: Pick<ICrise, 'id' | 'titre'> | null;
  offres?: Pick<IOffre, 'id'>[] | null;
}

export type NewDemande = Omit<IDemande, 'id'> & { id: null };
