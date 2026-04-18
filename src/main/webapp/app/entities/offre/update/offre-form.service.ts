import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOffre, NewOffre } from '../offre.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOffre for edit and NewOffreFormGroupInput for create.
 */
type OffreFormGroupInput = IOffre | PartialWithRequiredKeyOf<NewOffre>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOffre | NewOffre> = Omit<T, 'dateCreation' | 'dateMiseAJour'> & {
  dateCreation?: string | null;
  dateMiseAJour?: string | null;
};

type OffreFormRawValue = FormValueOf<IOffre>;

type NewOffreFormRawValue = FormValueOf<NewOffre>;

type OffreFormDefaults = Pick<NewOffre, 'id' | 'dateCreation' | 'dateMiseAJour' | 'archivee' | 'active' | 'demandes'>;

type OffreFormGroupContent = {
  id: FormControl<OffreFormRawValue['id'] | NewOffre['id']>;
  titre: FormControl<OffreFormRawValue['titre']>;
  description: FormControl<OffreFormRawValue['description']>;
  dateCreation: FormControl<OffreFormRawValue['dateCreation']>;
  dateMiseAJour: FormControl<OffreFormRawValue['dateMiseAJour']>;
  latitude: FormControl<OffreFormRawValue['latitude']>;
  longitude: FormControl<OffreFormRawValue['longitude']>;
  archivee: FormControl<OffreFormRawValue['archivee']>;
  active: FormControl<OffreFormRawValue['active']>;
  citoyen: FormControl<OffreFormRawValue['citoyen']>;
  crise: FormControl<OffreFormRawValue['crise']>;
  demandes: FormControl<OffreFormRawValue['demandes']>;
};

export type OffreFormGroup = FormGroup<OffreFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OffreFormService {
  createOffreFormGroup(offre?: OffreFormGroupInput): OffreFormGroup {
    const offreRawValue = this.convertOffreToOffreRawValue({
      ...this.getFormDefaults(),
      ...(offre ?? { id: null }),
    });
    return new FormGroup<OffreFormGroupContent>({
      id: new FormControl(
        { value: offreRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titre: new FormControl(offreRawValue.titre, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(200)],
      }),
      description: new FormControl(offreRawValue.description, {
        validators: [Validators.required, Validators.maxLength(2000)],
      }),
      dateCreation: new FormControl(offreRawValue.dateCreation, {
        validators: [Validators.required],
      }),
      dateMiseAJour: new FormControl(offreRawValue.dateMiseAJour),
      latitude: new FormControl(offreRawValue.latitude, {
        validators: [Validators.min(-90), Validators.max(90)],
      }),
      longitude: new FormControl(offreRawValue.longitude, {
        validators: [Validators.min(-180), Validators.max(180)],
      }),
      archivee: new FormControl(offreRawValue.archivee, {
        validators: [Validators.required],
      }),
      active: new FormControl(offreRawValue.active, {
        validators: [Validators.required],
      }),
      citoyen: new FormControl(offreRawValue.citoyen, {
        validators: [Validators.required],
      }),
      crise: new FormControl(offreRawValue.crise, {
        validators: [Validators.required],
      }),
      demandes: new FormControl(offreRawValue.demandes ?? []),
    });
  }

  getOffre(form: OffreFormGroup): IOffre | NewOffre {
    return this.convertOffreRawValueToOffre(form.getRawValue() as OffreFormRawValue | NewOffreFormRawValue);
  }

  resetForm(form: OffreFormGroup, offre: OffreFormGroupInput): void {
    const offreRawValue = this.convertOffreToOffreRawValue({ ...this.getFormDefaults(), ...offre });
    form.reset({
      ...offreRawValue,
      id: { value: offreRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): OffreFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreation: currentTime,
      dateMiseAJour: currentTime,
      archivee: false,
      active: false,
      demandes: [],
    };
  }

  private convertOffreRawValueToOffre(rawOffre: OffreFormRawValue | NewOffreFormRawValue): IOffre | NewOffre {
    return {
      ...rawOffre,
      dateCreation: dayjs(rawOffre.dateCreation, DATE_TIME_FORMAT),
      dateMiseAJour: dayjs(rawOffre.dateMiseAJour, DATE_TIME_FORMAT),
    };
  }

  private convertOffreToOffreRawValue(
    offre: IOffre | (Partial<NewOffre> & OffreFormDefaults),
  ): OffreFormRawValue | PartialWithRequiredKeyOf<NewOffreFormRawValue> {
    return {
      ...offre,
      dateCreation: offre.dateCreation ? offre.dateCreation.format(DATE_TIME_FORMAT) : undefined,
      dateMiseAJour: offre.dateMiseAJour ? offre.dateMiseAJour.format(DATE_TIME_FORMAT) : undefined,
      demandes: offre.demandes ?? [],
    };
  }
}
