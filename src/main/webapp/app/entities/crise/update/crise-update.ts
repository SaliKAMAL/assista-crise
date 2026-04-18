import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { StatutCrise } from 'app/entities/enumerations/statut-crise.model';
import { TypeCrise } from 'app/entities/enumerations/type-crise.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICrise } from '../crise.model';
import { CriseService } from '../service/crise.service';

import { CriseFormGroup, CriseFormService } from './crise-form.service';

@Component({
  selector: 'jhi-crise-update',
  templateUrl: './crise-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CriseUpdate implements OnInit {
  readonly isSaving = signal(false);
  crise: ICrise | null = null;
  typeCriseValues = Object.keys(TypeCrise);
  statutCriseValues = Object.keys(StatutCrise);

  usersSharedCollection = signal<IUser[]>([]);

  protected criseService = inject(CriseService);
  protected criseFormService = inject(CriseFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CriseFormGroup = this.criseFormService.createCriseFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ crise }) => {
      this.crise = crise;
      if (crise) {
        this.updateForm(crise);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const crise = this.criseFormService.getCrise(this.editForm);
    if (crise.id === null) {
      this.subscribeToSaveResponse(this.criseService.create(crise));
    } else {
      this.subscribeToSaveResponse(this.criseService.update(crise));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICrise | null>): void {
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

  protected updateForm(crise: ICrise): void {
    this.crise = crise;
    this.criseFormService.resetForm(this.editForm, crise);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, crise.declarant));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.crise?.declarant)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
