import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICrise } from 'app/entities/crise/crise.model';
import { CriseService } from 'app/entities/crise/service/crise.service';
import { IDemande } from 'app/entities/demande/demande.model';
import { DemandeService } from 'app/entities/demande/service/demande.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IOffre } from '../offre.model';
import { OffreService } from '../service/offre.service';

import { OffreFormService } from './offre-form.service';
import { OffreUpdate } from './offre-update';

describe('Offre Management Update Component', () => {
  let comp: OffreUpdate;
  let fixture: ComponentFixture<OffreUpdate>;
  let activatedRoute: ActivatedRoute;
  let offreFormService: OffreFormService;
  let offreService: OffreService;
  let userService: UserService;
  let criseService: CriseService;
  let demandeService: DemandeService;

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

    fixture = TestBed.createComponent(OffreUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    offreFormService = TestBed.inject(OffreFormService);
    offreService = TestBed.inject(OffreService);
    userService = TestBed.inject(UserService);
    criseService = TestBed.inject(CriseService);
    demandeService = TestBed.inject(DemandeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const offre: IOffre = { id: 8458 };
      const citoyen: IUser = { id: 3944 };
      offre.citoyen = citoyen;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [citoyen];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ offre });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Crise query and add missing value', () => {
      const offre: IOffre = { id: 8458 };
      const crise: ICrise = { id: 11332 };
      offre.crise = crise;

      const criseCollection: ICrise[] = [{ id: 11332 }];
      vitest.spyOn(criseService, 'query').mockReturnValue(of(new HttpResponse({ body: criseCollection })));
      const additionalCrises = [crise];
      const expectedCollection: ICrise[] = [...additionalCrises, ...criseCollection];
      vitest.spyOn(criseService, 'addCriseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ offre });
      comp.ngOnInit();

      expect(criseService.query).toHaveBeenCalled();
      expect(criseService.addCriseToCollectionIfMissing).toHaveBeenCalledWith(
        criseCollection,
        ...additionalCrises.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.crisesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Demande query and add missing value', () => {
      const offre: IOffre = { id: 8458 };
      const demandes: IDemande[] = [{ id: 27574 }];
      offre.demandes = demandes;

      const demandeCollection: IDemande[] = [{ id: 27574 }];
      vitest.spyOn(demandeService, 'query').mockReturnValue(of(new HttpResponse({ body: demandeCollection })));
      const additionalDemandes = [...demandes];
      const expectedCollection: IDemande[] = [...additionalDemandes, ...demandeCollection];
      vitest.spyOn(demandeService, 'addDemandeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ offre });
      comp.ngOnInit();

      expect(demandeService.query).toHaveBeenCalled();
      expect(demandeService.addDemandeToCollectionIfMissing).toHaveBeenCalledWith(
        demandeCollection,
        ...additionalDemandes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.demandesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const offre: IOffre = { id: 8458 };
      const citoyen: IUser = { id: 3944 };
      offre.citoyen = citoyen;
      const crise: ICrise = { id: 11332 };
      offre.crise = crise;
      const demande: IDemande = { id: 27574 };
      offre.demandes = [demande];

      activatedRoute.data = of({ offre });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(citoyen);
      expect(comp.crisesSharedCollection()).toContainEqual(crise);
      expect(comp.demandesSharedCollection()).toContainEqual(demande);
      expect(comp.offre).toEqual(offre);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IOffre>();
      const offre = { id: 9345 };
      vitest.spyOn(offreFormService, 'getOffre').mockReturnValue(offre);
      vitest.spyOn(offreService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ offre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(offre);
      saveSubject.complete();

      // THEN
      expect(offreFormService.getOffre).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(offreService.update).toHaveBeenCalledWith(expect.objectContaining(offre));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IOffre>();
      const offre = { id: 9345 };
      vitest.spyOn(offreFormService, 'getOffre').mockReturnValue({ id: null });
      vitest.spyOn(offreService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ offre: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(offre);
      saveSubject.complete();

      // THEN
      expect(offreFormService.getOffre).toHaveBeenCalled();
      expect(offreService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IOffre>();
      const offre = { id: 9345 };
      vitest.spyOn(offreService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ offre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(offreService.update).toHaveBeenCalled();
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

    describe('compareDemande', () => {
      it('should forward to demandeService', () => {
        const entity = { id: 27574 };
        const entity2 = { id: 24127 };
        vitest.spyOn(demandeService, 'compareDemande');
        comp.compareDemande(entity, entity2);
        expect(demandeService.compareDemande).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
