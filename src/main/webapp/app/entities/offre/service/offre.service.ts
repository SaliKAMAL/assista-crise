import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IOffre, NewOffre } from '../offre.model';

export type PartialUpdateOffre = Partial<IOffre> & Pick<IOffre, 'id'>;

type RestOf<T extends IOffre | NewOffre> = Omit<T, 'dateCreation' | 'dateMiseAJour'> & {
  dateCreation?: string | null;
  dateMiseAJour?: string | null;
};

export type RestOffre = RestOf<IOffre>;

export type NewRestOffre = RestOf<NewOffre>;

export type PartialUpdateRestOffre = RestOf<PartialUpdateOffre>;

@Injectable()
export class OffresService {
  readonly offresParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly offresResource = httpResource<RestOffre[]>(() => {
    const params = this.offresParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of offre that have been fetched. It is updated when the offresResource emits a new value.
   * In case of error while fetching the offres, the signal is set to an empty array.
   */
  readonly offres = computed(() =>
    (this.offresResource.hasValue() ? this.offresResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/offres');

  protected convertValueFromServer(restOffre: RestOffre): IOffre {
    return {
      ...restOffre,
      dateCreation: restOffre.dateCreation ? dayjs(restOffre.dateCreation) : undefined,
      dateMiseAJour: restOffre.dateMiseAJour ? dayjs(restOffre.dateMiseAJour) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class OffreService extends OffresService {
  protected readonly http = inject(HttpClient);

  create(offre: NewOffre): Observable<IOffre> {
    const copy = this.convertValueFromClient(offre);
    return this.http.post<RestOffre>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(offre: IOffre): Observable<IOffre> {
    const copy = this.convertValueFromClient(offre);
    return this.http
      .put<RestOffre>(`${this.resourceUrl}/${encodeURIComponent(this.getOffreIdentifier(offre))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(offre: PartialUpdateOffre): Observable<IOffre> {
    const copy = this.convertValueFromClient(offre);
    return this.http
      .patch<RestOffre>(`${this.resourceUrl}/${encodeURIComponent(this.getOffreIdentifier(offre))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IOffre> {
    return this.http.get<RestOffre>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IOffre[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOffre[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getOffreIdentifier(offre: Pick<IOffre, 'id'>): number {
    return offre.id;
  }

  compareOffre(o1: Pick<IOffre, 'id'> | null, o2: Pick<IOffre, 'id'> | null): boolean {
    return o1 && o2 ? this.getOffreIdentifier(o1) === this.getOffreIdentifier(o2) : o1 === o2;
  }

  addOffreToCollectionIfMissing<Type extends Pick<IOffre, 'id'>>(
    offreCollection: Type[],
    ...offresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const offres: Type[] = offresToCheck.filter(isPresent);
    if (offres.length > 0) {
      const offreCollectionIdentifiers = offreCollection.map(offreItem => this.getOffreIdentifier(offreItem));
      const offresToAdd = offres.filter(offreItem => {
        const offreIdentifier = this.getOffreIdentifier(offreItem);
        if (offreCollectionIdentifiers.includes(offreIdentifier)) {
          return false;
        }
        offreCollectionIdentifiers.push(offreIdentifier);
        return true;
      });
      return [...offresToAdd, ...offreCollection];
    }
    return offreCollection;
  }

  protected convertValueFromClient<T extends IOffre | NewOffre | PartialUpdateOffre>(offre: T): RestOf<T> {
    return {
      ...offre,
      dateCreation: offre.dateCreation?.toJSON() ?? null,
      dateMiseAJour: offre.dateMiseAJour?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestOffre): IOffre {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestOffre[]): IOffre[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
