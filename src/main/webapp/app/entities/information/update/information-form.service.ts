import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IInformation, NewInformation } from '../information.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInformation for edit and NewInformationFormGroupInput for create.
 */
type InformationFormGroupInput = IInformation | PartialWithRequiredKeyOf<NewInformation>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IInformation | NewInformation> = Omit<T, 'datePublication'> & {
  datePublication?: string | null;
};

type InformationFormRawValue = FormValueOf<IInformation>;

type NewInformationFormRawValue = FormValueOf<NewInformation>;

type InformationFormDefaults = Pick<NewInformation, 'id' | 'datePublication'>;

type InformationFormGroupContent = {
  id: FormControl<InformationFormRawValue['id'] | NewInformation['id']>;
  titre: FormControl<InformationFormRawValue['titre']>;
  contenu: FormControl<InformationFormRawValue['contenu']>;
  datePublication: FormControl<InformationFormRawValue['datePublication']>;
  latitude: FormControl<InformationFormRawValue['latitude']>;
  longitude: FormControl<InformationFormRawValue['longitude']>;
  auteur: FormControl<InformationFormRawValue['auteur']>;
  crise: FormControl<InformationFormRawValue['crise']>;
};

export type InformationFormGroup = FormGroup<InformationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InformationFormService {
  createInformationFormGroup(information?: InformationFormGroupInput): InformationFormGroup {
    const informationRawValue = this.convertInformationToInformationRawValue({
      ...this.getFormDefaults(),
      ...(information ?? { id: null }),
    });
    return new FormGroup<InformationFormGroupContent>({
      id: new FormControl(
        { value: informationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titre: new FormControl(informationRawValue.titre, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(200)],
      }),
      contenu: new FormControl(informationRawValue.contenu, {
        validators: [Validators.required, Validators.maxLength(5000)],
      }),
      datePublication: new FormControl(informationRawValue.datePublication, {
        validators: [Validators.required],
      }),
      latitude: new FormControl(informationRawValue.latitude, {
        validators: [Validators.min(-90), Validators.max(90)],
      }),
      longitude: new FormControl(informationRawValue.longitude, {
        validators: [Validators.min(-180), Validators.max(180)],
      }),
      auteur: new FormControl(informationRawValue.auteur, {
        validators: [Validators.required],
      }),
      crise: new FormControl(informationRawValue.crise, {
        validators: [Validators.required],
      }),
    });
  }

  getInformation(form: InformationFormGroup): IInformation | NewInformation {
    return this.convertInformationRawValueToInformation(form.getRawValue() as InformationFormRawValue | NewInformationFormRawValue);
  }

  resetForm(form: InformationFormGroup, information: InformationFormGroupInput): void {
    const informationRawValue = this.convertInformationToInformationRawValue({ ...this.getFormDefaults(), ...information });
    form.reset({
      ...informationRawValue,
      id: { value: informationRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): InformationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      datePublication: currentTime,
    };
  }

  private convertInformationRawValueToInformation(
    rawInformation: InformationFormRawValue | NewInformationFormRawValue,
  ): IInformation | NewInformation {
    return {
      ...rawInformation,
      datePublication: dayjs(rawInformation.datePublication, DATE_TIME_FORMAT),
    };
  }

  private convertInformationToInformationRawValue(
    information: IInformation | (Partial<NewInformation> & InformationFormDefaults),
  ): InformationFormRawValue | PartialWithRequiredKeyOf<NewInformationFormRawValue> {
    return {
      ...information,
      datePublication: information.datePublication ? information.datePublication.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
