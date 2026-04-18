import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICrise, NewCrise } from '../crise.model';

export type PartialUpdateCrise = Partial<ICrise> & Pick<ICrise, 'id'>;

type RestOf<T extends ICrise | NewCrise> = Omit<T, 'dateDebut' | 'dateFermeture'> & {
  dateDebut?: string | null;
  dateFermeture?: string | null;
};

export type RestCrise = RestOf<ICrise>;

export type NewRestCrise = RestOf<NewCrise>;

export type PartialUpdateRestCrise = RestOf<PartialUpdateCrise>;

@Injectable()
export class CrisesService {
  readonly crisesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly crisesResource = httpResource<RestCrise[]>(() => {
    const params = this.crisesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of crise that have been fetched. It is updated when the crisesResource emits a new value.
   * In case of error while fetching the crises, the signal is set to an empty array.
   */
  readonly crises = computed(() =>
    (this.crisesResource.hasValue() ? this.crisesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/crises');

  protected convertValueFromServer(restCrise: RestCrise): ICrise {
    return {
      ...restCrise,
      dateDebut: restCrise.dateDebut ? dayjs(restCrise.dateDebut) : undefined,
      dateFermeture: restCrise.dateFermeture ? dayjs(restCrise.dateFermeture) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class CriseService extends CrisesService {
  protected readonly http = inject(HttpClient);

  create(crise: NewCrise): Observable<ICrise> {
    const copy = this.convertValueFromClient(crise);
    return this.http.post<RestCrise>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(crise: ICrise): Observable<ICrise> {
    const copy = this.convertValueFromClient(crise);
    return this.http
      .put<RestCrise>(`${this.resourceUrl}/${encodeURIComponent(this.getCriseIdentifier(crise))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(crise: PartialUpdateCrise): Observable<ICrise> {
    const copy = this.convertValueFromClient(crise);
    return this.http
      .patch<RestCrise>(`${this.resourceUrl}/${encodeURIComponent(this.getCriseIdentifier(crise))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ICrise> {
    return this.http.get<RestCrise>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICrise[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCrise[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCriseIdentifier(crise: Pick<ICrise, 'id'>): number {
    return crise.id;
  }

  compareCrise(o1: Pick<ICrise, 'id'> | null, o2: Pick<ICrise, 'id'> | null): boolean {
    return o1 && o2 ? this.getCriseIdentifier(o1) === this.getCriseIdentifier(o2) : o1 === o2;
  }

  addCriseToCollectionIfMissing<Type extends Pick<ICrise, 'id'>>(
    criseCollection: Type[],
    ...crisesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const crises: Type[] = crisesToCheck.filter(isPresent);
    if (crises.length > 0) {
      const criseCollectionIdentifiers = criseCollection.map(criseItem => this.getCriseIdentifier(criseItem));
      const crisesToAdd = crises.filter(criseItem => {
        const criseIdentifier = this.getCriseIdentifier(criseItem);
        if (criseCollectionIdentifiers.includes(criseIdentifier)) {
          return false;
        }
        criseCollectionIdentifiers.push(criseIdentifier);
        return true;
      });
      return [...crisesToAdd, ...criseCollection];
    }
    return criseCollection;
  }

  protected convertValueFromClient<T extends ICrise | NewCrise | PartialUpdateCrise>(crise: T): RestOf<T> {
    return {
      ...crise,
      dateDebut: crise.dateDebut?.toJSON() ?? null,
      dateFermeture: crise.dateFermeture?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCrise): ICrise {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCrise[]): ICrise[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
