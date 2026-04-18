import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDemande, NewDemande } from '../demande.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDemande for edit and NewDemandeFormGroupInput for create.
 */
type DemandeFormGroupInput = IDemande | PartialWithRequiredKeyOf<NewDemande>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDemande | NewDemande> = Omit<T, 'dateCreation' | 'dateMiseAJour'> & {
  dateCreation?: string | null;
  dateMiseAJour?: string | null;
};

type DemandeFormRawValue = FormValueOf<IDemande>;

type NewDemandeFormRawValue = FormValueOf<NewDemande>;

type DemandeFormDefaults = Pick<NewDemande, 'id' | 'dateCreation' | 'dateMiseAJour' | 'archivee' | 'offres'>;

type DemandeFormGroupContent = {
  id: FormControl<DemandeFormRawValue['id'] | NewDemande['id']>;
  titre: FormControl<DemandeFormRawValue['titre']>;
  description: FormControl<DemandeFormRawValue['description']>;
  statut: FormControl<DemandeFormRawValue['statut']>;
  dateCreation: FormControl<DemandeFormRawValue['dateCreation']>;
  dateMiseAJour: FormControl<DemandeFormRawValue['dateMiseAJour']>;
  latitude: FormControl<DemandeFormRawValue['latitude']>;
  longitude: FormControl<DemandeFormRawValue['longitude']>;
  archivee: FormControl<DemandeFormRawValue['archivee']>;
  sinistre: FormControl<DemandeFormRawValue['sinistre']>;
  crise: FormControl<DemandeFormRawValue['crise']>;
  offres: FormControl<DemandeFormRawValue['offres']>;
};

export type DemandeFormGroup = FormGroup<DemandeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DemandeFormService {
  createDemandeFormGroup(demande?: DemandeFormGroupInput): DemandeFormGroup {
    const demandeRawValue = this.convertDemandeToDemandeRawValue({
      ...this.getFormDefaults(),
      ...(demande ?? { id: null }),
    });
    return new FormGroup<DemandeFormGroupContent>({
      id: new FormControl(
        { value: demandeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titre: new FormControl(demandeRawValue.titre, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(200)],
      }),
      description: new FormControl(demandeRawValue.description, {
        validators: [Validators.required, Validators.maxLength(2000)],
      }),
      statut: new FormControl(demandeRawValue.statut, {
        validators: [Validators.required],
      }),
      dateCreation: new FormControl(demandeRawValue.dateCreation, {
        validators: [Validators.required],
      }),
      dateMiseAJour: new FormControl(demandeRawValue.dateMiseAJour),
      latitude: new FormControl(demandeRawValue.latitude, {
        validators: [Validators.min(-90), Validators.max(90)],
      }),
      longitude: new FormControl(demandeRawValue.longitude, {
        validators: [Validators.min(-180), Validators.max(180)],
      }),
      archivee: new FormControl(demandeRawValue.archivee, {
        validators: [Validators.required],
      }),
      sinistre: new FormControl(demandeRawValue.sinistre, {
        validators: [Validators.required],
      }),
      crise: new FormControl(demandeRawValue.crise, {
        validators: [Validators.required],
      }),
      offres: new FormControl(demandeRawValue.offres ?? []),
    });
  }

  getDemande(form: DemandeFormGroup): IDemande | NewDemande {
    return this.convertDemandeRawValueToDemande(form.getRawValue() as DemandeFormRawValue | NewDemandeFormRawValue);
  }

  resetForm(form: DemandeFormGroup, demande: DemandeFormGroupInput): void {
    const demandeRawValue = this.convertDemandeToDemandeRawValue({ ...this.getFormDefaults(), ...demande });
    form.reset({
      ...demandeRawValue,
      id: { value: demandeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DemandeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreation: currentTime,
      dateMiseAJour: currentTime,
      archivee: false,
      offres: [],
    };
  }

  private convertDemandeRawValueToDemande(rawDemande: DemandeFormRawValue | NewDemandeFormRawValue): IDemande | NewDemande {
    return {
      ...rawDemande,
      dateCreation: dayjs(rawDemande.dateCreation, DATE_TIME_FORMAT),
      dateMiseAJour: dayjs(rawDemande.dateMiseAJour, DATE_TIME_FORMAT),
    };
  }

  private convertDemandeToDemandeRawValue(
    demande: IDemande | (Partial<NewDemande> & DemandeFormDefaults),
  ): DemandeFormRawValue | PartialWithRequiredKeyOf<NewDemandeFormRawValue> {
    return {
      ...demande,
      dateCreation: demande.dateCreation ? demande.dateCreation.format(DATE_TIME_FORMAT) : undefined,
      dateMiseAJour: demande.dateMiseAJour ? demande.dateMiseAJour.format(DATE_TIME_FORMAT) : undefined,
      offres: demande.offres ?? [],
    };
  }
}
