import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IInformation, NewInformation } from '../information.model';

export type PartialUpdateInformation = Partial<IInformation> & Pick<IInformation, 'id'>;

type RestOf<T extends IInformation | NewInformation> = Omit<T, 'datePublication'> & {
  datePublication?: string | null;
};

export type RestInformation = RestOf<IInformation>;

export type NewRestInformation = RestOf<NewInformation>;

export type PartialUpdateRestInformation = RestOf<PartialUpdateInformation>;

@Injectable()
export class InformationsService {
  readonly informationsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly informationsResource = httpResource<RestInformation[]>(() => {
    const params = this.informationsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of information that have been fetched. It is updated when the informationsResource emits a new value.
   * In case of error while fetching the informations, the signal is set to an empty array.
   */
  readonly informations = computed(() =>
    (this.informationsResource.hasValue() ? this.informationsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/information');

  protected convertValueFromServer(restInformation: RestInformation): IInformation {
    return {
      ...restInformation,
      datePublication: restInformation.datePublication ? dayjs(restInformation.datePublication) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class InformationService extends InformationsService {
  protected readonly http = inject(HttpClient);

  create(information: NewInformation): Observable<IInformation> {
    const copy = this.convertValueFromClient(information);
    return this.http.post<RestInformation>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(information: IInformation): Observable<IInformation> {
    const copy = this.convertValueFromClient(information);
    return this.http
      .put<RestInformation>(`${this.resourceUrl}/${encodeURIComponent(this.getInformationIdentifier(information))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(information: PartialUpdateInformation): Observable<IInformation> {
    const copy = this.convertValueFromClient(information);
    return this.http
      .patch<RestInformation>(`${this.resourceUrl}/${encodeURIComponent(this.getInformationIdentifier(information))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IInformation> {
    return this.http
      .get<RestInformation>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IInformation[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestInformation[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getInformationIdentifier(information: Pick<IInformation, 'id'>): number {
    return information.id;
  }

  compareInformation(o1: Pick<IInformation, 'id'> | null, o2: Pick<IInformation, 'id'> | null): boolean {
    return o1 && o2 ? this.getInformationIdentifier(o1) === this.getInformationIdentifier(o2) : o1 === o2;
  }

  addInformationToCollectionIfMissing<Type extends Pick<IInformation, 'id'>>(
    informationCollection: Type[],
    ...informationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const informations: Type[] = informationsToCheck.filter(isPresent);
    if (informations.length > 0) {
      const informationCollectionIdentifiers = informationCollection.map(informationItem => this.getInformationIdentifier(informationItem));
      const informationsToAdd = informations.filter(informationItem => {
        const informationIdentifier = this.getInformationIdentifier(informationItem);
        if (informationCollectionIdentifiers.includes(informationIdentifier)) {
          return false;
        }
        informationCollectionIdentifiers.push(informationIdentifier);
        return true;
      });
      return [...informationsToAdd, ...informationCollection];
    }
    return informationCollection;
  }

  protected convertValueFromClient<T extends IInformation | NewInformation | PartialUpdateInformation>(information: T): RestOf<T> {
    return {
      ...information,
      datePublication: information.datePublication?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestInformation): IInformation {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestInformation[]): IInformation[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
