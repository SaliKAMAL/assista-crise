import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICrise } from 'app/entities/crise/crise.model';
import { CriseService } from 'app/entities/crise/service/crise.service';
import { StatutDemande } from 'app/entities/enumerations/statut-demande.model';
import { IOffre } from 'app/entities/offre/offre.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IDemande } from '../demande.model';
import { DemandeService } from '../service/demande.service';

import { DemandeFormGroup, DemandeFormService } from './demande-form.service';
import { OffreService } from 'app/entities/offre/service/offre.service';

@Component({
  selector: 'jhi-demande-update',
  templateUrl: './demande-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class DemandeUpdate implements OnInit {
  readonly isSaving = signal(false);
  demande: IDemande | null = null;
  statutDemandeValues = Object.keys(StatutDemande);

  usersSharedCollection = signal<IUser[]>([]);
  crisesSharedCollection = signal<ICrise[]>([]);
  offresSharedCollection = signal<IOffre[]>([]);

  protected demandeService = inject(DemandeService);
  protected demandeFormService = inject(DemandeFormService);
  protected userService = inject(UserService);
  protected criseService = inject(CriseService);
  protected offreService = inject(OffreService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DemandeFormGroup = this.demandeFormService.createDemandeFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareCrise = (o1: ICrise | null, o2: ICrise | null): boolean => this.criseService.compareCrise(o1, o2);

  compareOffre = (o1: IOffre | null, o2: IOffre | null): boolean => this.offreService.compareOffre(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ demande }) => {
      this.demande = demande;
      if (demande) {
        this.updateForm(demande);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const demande = this.demandeFormService.getDemande(this.editForm);
    if (demande.id === null) {
      this.subscribeToSaveResponse(this.demandeService.create(demande));
    } else {
      this.subscribeToSaveResponse(this.demandeService.update(demande));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IDemande | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(demande: IDemande): void {
    this.demande = demande;
    this.demandeFormService.resetForm(this.editForm, demande);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, demande.sinistre));
    this.crisesSharedCollection.update(crises => this.criseService.addCriseToCollectionIfMissing<ICrise>(crises, demande.crise));
    this.offresSharedCollection.update(offres =>
      this.offreService.addOffreToCollectionIfMissing<IOffre>(offres, ...(demande.offres ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.demande?.sinistre)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));

    this.criseService
      .query()
      .pipe(map((res: HttpResponse<ICrise[]>) => res.body ?? []))
      .pipe(map((crises: ICrise[]) => this.criseService.addCriseToCollectionIfMissing<ICrise>(crises, this.demande?.crise)))
      .subscribe((crises: ICrise[]) => this.crisesSharedCollection.set(crises));

    this.offreService
      .query()
      .pipe(map((res: HttpResponse<IOffre[]>) => res.body ?? []))
      .pipe(map((offres: IOffre[]) => this.offreService.addOffreToCollectionIfMissing<IOffre>(offres, ...(this.demande?.offres ?? []))))
      .subscribe((offres: IOffre[]) => this.offresSharedCollection.set(offres));
  }
}
