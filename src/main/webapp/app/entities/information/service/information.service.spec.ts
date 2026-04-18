import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IInformation } from '../information.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../information.test-samples';

import { InformationService, RestInformation } from './information.service';

const requireRestSample: RestInformation = {
  ...sampleWithRequiredData,
  datePublication: sampleWithRequiredData.datePublication?.toJSON(),
};

describe('Information Service', () => {
  let service: InformationService;
  let httpMock: HttpTestingController;
  let expectedResult: IInformation | IInformation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(InformationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Information', () => {
      const information = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(information).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Information', () => {
      const information = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(information).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Information', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Information', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Information', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addInformationToCollectionIfMissing', () => {
      it('should add a Information to an empty array', () => {
        const information: IInformation = sampleWithRequiredData;
        expectedResult = service.addInformationToCollectionIfMissing([], information);
        expect(expectedResult).toEqual([information]);
      });

      it('should not add a Information to an array that contains it', () => {
        const information: IInformation = sampleWithRequiredData;
        const informationCollection: IInformation[] = [
          {
            ...information,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addInformationToCollectionIfMissing(informationCollection, information);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Information to an array that doesn't contain it", () => {
        const information: IInformation = sampleWithRequiredData;
        const informationCollection: IInformation[] = [sampleWithPartialData];
        expectedResult = service.addInformationToCollectionIfMissing(informationCollection, information);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(information);
      });

      it('should add only unique Information to an array', () => {
        const informationArray: IInformation[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const informationCollection: IInformation[] = [sampleWithRequiredData];
        expectedResult = service.addInformationToCollectionIfMissing(informationCollection, ...informationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const information: IInformation = sampleWithRequiredData;
        const information2: IInformation = sampleWithPartialData;
        expectedResult = service.addInformationToCollectionIfMissing([], information, information2);
        expect(expectedResult).toEqual([information, information2]);
      });

      it('should accept null and undefined values', () => {
        const information: IInformation = sampleWithRequiredData;
        expectedResult = service.addInformationToCollectionIfMissing([], null, information, undefined);
        expect(expectedResult).toEqual([information]);
      });

      it('should return initial array if no Information is added', () => {
        const informationCollection: IInformation[] = [sampleWithRequiredData];
        expectedResult = service.addInformationToCollectionIfMissing(informationCollection, undefined, null);
        expect(expectedResult).toEqual(informationCollection);
      });
    });

    describe('compareInformation', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareInformation(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 27708 };
        const entity2 = null;

        const compareResult1 = service.compareInformation(entity1, entity2);
        const compareResult2 = service.compareInformation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 27708 };
        const entity2 = { id: 23476 };

        const compareResult1 = service.compareInformation(entity1, entity2);
        const compareResult2 = service.compareInformation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 27708 };
        const entity2 = { id: 27708 };

        const compareResult1 = service.compareInformation(entity1, entity2);
        const compareResult2 = service.compareInformation(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
