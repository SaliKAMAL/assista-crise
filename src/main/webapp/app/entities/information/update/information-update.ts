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
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IInformation } from '../information.model';
import { InformationService } from '../service/information.service';

import { InformationFormGroup, InformationFormService } from './information-form.service';

@Component({
  selector: 'jhi-information-update',
  templateUrl: './information-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class InformationUpdate implements OnInit {
  readonly isSaving = signal(false);
  information: IInformation | null = null;

  usersSharedCollection = signal<IUser[]>([]);
  crisesSharedCollection = signal<ICrise[]>([]);

  protected informationService = inject(InformationService);
  protected informationFormService = inject(InformationFormService);
  protected userService = inject(UserService);
  protected criseService = inject(CriseService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InformationFormGroup = this.informationFormService.createInformationFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareCrise = (o1: ICrise | null, o2: ICrise | null): boolean => this.criseService.compareCrise(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ information }) => {
      this.information = information;
      if (information) {
        this.updateForm(information);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const information = this.informationFormService.getInformation(this.editForm);
    if (information.id === null) {
      this.subscribeToSaveResponse(this.informationService.create(information));
    } else {
      this.subscribeToSaveResponse(this.informationService.update(information));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IInformation | null>): void {
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

  protected updateForm(information: IInformation): void {
    this.information = information;
    this.informationFormService.resetForm(this.editForm, information);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, information.auteur));
    this.crisesSharedCollection.update(crises => this.criseService.addCriseToCollectionIfMissing<ICrise>(crises, information.crise));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.information?.auteur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));

    this.criseService
      .query()
      .pipe(map((res: HttpResponse<ICrise[]>) => res.body ?? []))
      .pipe(map((crises: ICrise[]) => this.criseService.addCriseToCollectionIfMissing<ICrise>(crises, this.information?.crise)))
      .subscribe((crises: ICrise[]) => this.crisesSharedCollection.set(crises));
  }
}
