import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { ICrise } from '../crise.model';
import { CriseService } from '../service/crise.service';

import { CriseFormService } from './crise-form.service';
import { CriseUpdate } from './crise-update';

describe('Crise Management Update Component', () => {
  let comp: CriseUpdate;
  let fixture: ComponentFixture<CriseUpdate>;
  let activatedRoute: ActivatedRoute;
  let criseFormService: CriseFormService;
  let criseService: CriseService;
  let userService: UserService;

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

    fixture = TestBed.createComponent(CriseUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    criseFormService = TestBed.inject(CriseFormService);
    criseService = TestBed.inject(CriseService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const crise: ICrise = { id: 22123 };
      const declarant: IUser = { id: 3944 };
      crise.declarant = declarant;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [declarant];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ crise });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const crise: ICrise = { id: 22123 };
      const declarant: IUser = { id: 3944 };
      crise.declarant = declarant;

      activatedRoute.data = of({ crise });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(declarant);
      expect(comp.crise).toEqual(crise);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICrise>();
      const crise = { id: 11332 };
      vitest.spyOn(criseFormService, 'getCrise').mockReturnValue(crise);
      vitest.spyOn(criseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ crise });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(crise);
      saveSubject.complete();

      // THEN
      expect(criseFormService.getCrise).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(criseService.update).toHaveBeenCalledWith(expect.objectContaining(crise));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICrise>();
      const crise = { id: 11332 };
      vitest.spyOn(criseFormService, 'getCrise').mockReturnValue({ id: null });
      vitest.spyOn(criseService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ crise: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(crise);
      saveSubject.complete();

      // THEN
      expect(criseFormService.getCrise).toHaveBeenCalled();
      expect(criseService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICrise>();
      const crise = { id: 11332 };
      vitest.spyOn(criseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ crise });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(criseService.update).toHaveBeenCalled();
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
  });
});
