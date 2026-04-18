import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../information.test-samples';

import { InformationFormService } from './information-form.service';

describe('Information Form Service', () => {
  let service: InformationFormService;

  beforeEach(() => {
    service = TestBed.inject(InformationFormService);
  });

  describe('Service methods', () => {
    describe('createInformationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createInformationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titre: expect.any(Object),
            contenu: expect.any(Object),
            datePublication: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            auteur: expect.any(Object),
            crise: expect.any(Object),
          }),
        );
      });

      it('passing IInformation should create a new form with FormGroup', () => {
        const formGroup = service.createInformationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titre: expect.any(Object),
            contenu: expect.any(Object),
            datePublication: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            auteur: expect.any(Object),
            crise: expect.any(Object),
          }),
        );
      });
    });

    describe('getInformation', () => {
      it('should return NewInformation for default Information initial value', () => {
        const formGroup = service.createInformationFormGroup(sampleWithNewData);

        const information = service.getInformation(formGroup);

        expect(information).toMatchObject(sampleWithNewData);
      });

      it('should return NewInformation for empty Information initial value', () => {
        const formGroup = service.createInformationFormGroup();

        const information = service.getInformation(formGroup);

        expect(information).toMatchObject({});
      });

      it('should return IInformation', () => {
        const formGroup = service.createInformationFormGroup(sampleWithRequiredData);

        const information = service.getInformation(formGroup);

        expect(information).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IInformation should not enable id FormControl', () => {
        const formGroup = service.createInformationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewInformation should disable id FormControl', () => {
        const formGroup = service.createInformationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
