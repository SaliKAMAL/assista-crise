import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICrise, NewCrise } from '../crise.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICrise for edit and NewCriseFormGroupInput for create.
 */
type CriseFormGroupInput = ICrise | PartialWithRequiredKeyOf<NewCrise>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICrise | NewCrise> = Omit<T, 'dateDebut' | 'dateFermeture'> & {
  dateDebut?: string | null;
  dateFermeture?: string | null;
};

type CriseFormRawValue = FormValueOf<ICrise>;

type NewCriseFormRawValue = FormValueOf<NewCrise>;

type CriseFormDefaults = Pick<NewCrise, 'id' | 'dateDebut' | 'dateFermeture'>;

type CriseFormGroupContent = {
  id: FormControl<CriseFormRawValue['id'] | NewCrise['id']>;
  titre: FormControl<CriseFormRawValue['titre']>;
  description: FormControl<CriseFormRawValue['description']>;
  type: FormControl<CriseFormRawValue['type']>;
  dateDebut: FormControl<CriseFormRawValue['dateDebut']>;
  dateFermeture: FormControl<CriseFormRawValue['dateFermeture']>;
  statut: FormControl<CriseFormRawValue['statut']>;
  latitude: FormControl<CriseFormRawValue['latitude']>;
  longitude: FormControl<CriseFormRawValue['longitude']>;
  declarant: FormControl<CriseFormRawValue['declarant']>;
};

export type CriseFormGroup = FormGroup<CriseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CriseFormService {
  createCriseFormGroup(crise?: CriseFormGroupInput): CriseFormGroup {
    const criseRawValue = this.convertCriseToCriseRawValue({
      ...this.getFormDefaults(),
      ...(crise ?? { id: null }),
    });
    return new FormGroup<CriseFormGroupContent>({
      id: new FormControl(
        { value: criseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titre: new FormControl(criseRawValue.titre, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(200)],
      }),
      description: new FormControl(criseRawValue.description, {
        validators: [Validators.maxLength(2000)],
      }),
      type: new FormControl(criseRawValue.type, {
        validators: [Validators.required],
      }),
      dateDebut: new FormControl(criseRawValue.dateDebut, {
        validators: [Validators.required],
      }),
      dateFermeture: new FormControl(criseRawValue.dateFermeture),
      statut: new FormControl(criseRawValue.statut, {
        validators: [Validators.required],
      }),
      latitude: new FormControl(criseRawValue.latitude, {
        validators: [Validators.required, Validators.min(-90), Validators.max(90)],
      }),
      longitude: new FormControl(criseRawValue.longitude, {
        validators: [Validators.required, Validators.min(-180), Validators.max(180)],
      }),
      declarant: new FormControl(criseRawValue.declarant, {
        validators: [Validators.required],
      }),
    });
  }

  getCrise(form: CriseFormGroup): ICrise | NewCrise {
    return this.convertCriseRawValueToCrise(form.getRawValue() as CriseFormRawValue | NewCriseFormRawValue);
  }

  resetForm(form: CriseFormGroup, crise: CriseFormGroupInput): void {
    const criseRawValue = this.convertCriseToCriseRawValue({ ...this.getFormDefaults(), ...crise });
    form.reset({
      ...criseRawValue,
      id: { value: criseRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CriseFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateDebut: currentTime,
      dateFermeture: currentTime,
    };
  }

  private convertCriseRawValueToCrise(rawCrise: CriseFormRawValue | NewCriseFormRawValue): ICrise | NewCrise {
    return {
      ...rawCrise,
      dateDebut: dayjs(rawCrise.dateDebut, DATE_TIME_FORMAT),
      dateFermeture: dayjs(rawCrise.dateFermeture, DATE_TIME_FORMAT),
    };
  }

  private convertCriseToCriseRawValue(
    crise: ICrise | (Partial<NewCrise> & CriseFormDefaults),
  ): CriseFormRawValue | PartialWithRequiredKeyOf<NewCriseFormRawValue> {
    return {
      ...crise,
      dateDebut: crise.dateDebut ? crise.dateDebut.format(DATE_TIME_FORMAT) : undefined,
      dateFermeture: crise.dateFermeture ? crise.dateFermeture.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
