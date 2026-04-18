import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICrise } from 'app/entities/crise/crise.model';
import { CriseService } from 'app/entities/crise/service/crise.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IInformation } from '../information.model';
import { InformationService } from '../service/information.service';

import { InformationFormService } from './information-form.service';
import { InformationUpdate } from './information-update';

describe('Information Management Update Component', () => {
  let comp: InformationUpdate;
  let fixture: ComponentFixture<InformationUpdate>;
  let activatedRoute: ActivatedRoute;
  let informationFormService: InformationFormService;
  let informationService: InformationService;
  let userService: UserService;
  let criseService: CriseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(InformationUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    informationFormService = TestBed.inject(InformationFormService);
    informationService = TestBed.inject(InformationService);
    userService = TestBed.inject(UserService);
    criseService = TestBed.inject(CriseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const information: IInformation = { id: 23476 };
      const auteur: IUser = { id: 3944 };
      information.auteur = auteur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [auteur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ information });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Crise query and add missing value', () => {
      const information: IInformation = { id: 23476 };
      const crise: ICrise = { id: 11332 };
      information.crise = crise;

      const criseCollection: ICrise[] = [{ id: 11332 }];
      vitest.spyOn(criseService, 'query').mockReturnValue(of(new HttpResponse({ body: criseCollection })));
      const additionalCrises = [crise];
      const expectedCollection: ICrise[] = [...additionalCrises, ...criseCollection];
      vitest.spyOn(criseService, 'addCriseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ information });
      comp.ngOnInit();

      expect(criseService.query).toHaveBeenCalled();
      expect(criseService.addCriseToCollectionIfMissing).toHaveBeenCalledWith(
        criseCollection,
        ...additionalCrises.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.crisesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const information: IInformation = { id: 23476 };
      const auteur: IUser = { id: 3944 };
      information.auteur = auteur;
      const crise: ICrise = { id: 11332 };
      information.crise = crise;

      activatedRoute.data = of({ information });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(auteur);
      expect(comp.crisesSharedCollection()).toContainEqual(crise);
      expect(comp.information).toEqual(information);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IInformation>();
      const information = { id: 27708 };
      vitest.spyOn(informationFormService, 'getInformation').mockReturnValue(information);
      vitest.spyOn(informationService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ information });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(information);
      saveSubject.complete();

      // THEN
      expect(informationFormService.getInformation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(informationService.update).toHaveBeenCalledWith(expect.objectContaining(information));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IInformation>();
      const information = { id: 27708 };
      vitest.spyOn(informationFormService, 'getInformation').mockReturnValue({ id: null });
      vitest.spyOn(informationService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ information: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(information);
      saveSubject.complete();

      // THEN
      expect(informationFormService.getInformation).toHaveBeenCalled();
      expect(informationService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IInformation>();
      const information = { id: 27708 };
      vitest.spyOn(informationService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ information });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(informationService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCrise', () => {
      it('should forward to criseService', () => {
        const entity = { id: 11332 };
        const entity2 = { id: 22123 };
        vitest.spyOn(criseService, 'compareCrise');
        comp.compareCrise(entity, entity2);
        expect(criseService.compareCrise).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
