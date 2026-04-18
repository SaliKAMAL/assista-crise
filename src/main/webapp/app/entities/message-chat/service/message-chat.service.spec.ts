import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IMessageChat } from '../message-chat.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../message-chat.test-samples';

import { MessageChatService, RestMessageChat } from './message-chat.service';

const requireRestSample: RestMessageChat = {
  ...sampleWithRequiredData,
  dateEnvoi: sampleWithRequiredData.dateEnvoi?.toJSON(),
};

describe('MessageChat Service', () => {
  let service: MessageChatService;
  let httpMock: HttpTestingController;
  let expectedResult: IMessageChat | IMessageChat[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MessageChatService);
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

    it('should create a MessageChat', () => {
      const messageChat = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(messageChat).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MessageChat', () => {
      const messageChat = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(messageChat).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MessageChat', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MessageChat', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MessageChat', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addMessageChatToCollectionIfMissing', () => {
      it('should add a MessageChat to an empty array', () => {
        const messageChat: IMessageChat = sampleWithRequiredData;
        expectedResult = service.addMessageChatToCollectionIfMissing([], messageChat);
        expect(expectedResult).toEqual([messageChat]);
      });

      it('should not add a MessageChat to an array that contains it', () => {
        const messageChat: IMessageChat = sampleWithRequiredData;
        const messageChatCollection: IMessageChat[] = [
          {
            ...messageChat,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMessageChatToCollectionIfMissing(messageChatCollection, messageChat);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MessageChat to an array that doesn't contain it", () => {
        const messageChat: IMessageChat = sampleWithRequiredData;
        const messageChatCollection: IMessageChat[] = [sampleWithPartialData];
        expectedResult = service.addMessageChatToCollectionIfMissing(messageChatCollection, messageChat);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(messageChat);
      });

      it('should add only unique MessageChat to an array', () => {
        const messageChatArray: IMessageChat[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const messageChatCollection: IMessageChat[] = [sampleWithRequiredData];
        expectedResult = service.addMessageChatToCollectionIfMissing(messageChatCollection, ...messageChatArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const messageChat: IMessageChat = sampleWithRequiredData;
        const messageChat2: IMessageChat = sampleWithPartialData;
        expectedResult = service.addMessageChatToCollectionIfMissing([], messageChat, messageChat2);
        expect(expectedResult).toEqual([messageChat, messageChat2]);
      });

      it('should accept null and undefined values', () => {
        const messageChat: IMessageChat = sampleWithRequiredData;
        expectedResult = service.addMessageChatToCollectionIfMissing([], null, messageChat, undefined);
        expect(expectedResult).toEqual([messageChat]);
      });

      it('should return initial array if no MessageChat is added', () => {
        const messageChatCollection: IMessageChat[] = [sampleWithRequiredData];
        expectedResult = service.addMessageChatToCollectionIfMissing(messageChatCollection, undefined, null);
        expect(expectedResult).toEqual(messageChatCollection);
      });
    });

    describe('compareMessageChat', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMessageChat(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 6277 };
        const entity2 = null;

        const compareResult1 = service.compareMessageChat(entity1, entity2);
        const compareResult2 = service.compareMessageChat(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 6277 };
        const entity2 = { id: 25461 };

        const compareResult1 = service.compareMessageChat(entity1, entity2);
        const compareResult2 = service.compareMessageChat(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 6277 };
        const entity2 = { id: 6277 };

        const compareResult1 = service.compareMessageChat(entity1, entity2);
        const compareResult2 = service.compareMessageChat(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
