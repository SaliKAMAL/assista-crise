import dayjs from 'dayjs/esm';

import { IDemande } from 'app/entities/demande/demande.model';
import { IUser } from 'app/entities/user/user.model';

export interface IMessageChat {
  id: number;
  contenu?: string | null;
  dateEnvoi?: dayjs.Dayjs | null;
  auteur?: Pick<IUser, 'id' | 'login'> | null;
  demande?: Pick<IDemande, 'id' | 'titre'> | null;
}

export type NewMessageChat = Omit<IMessageChat, 'id'> & { id: null };
