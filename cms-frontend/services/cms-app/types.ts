export interface ResponseWrapper<T> {
  responseCode: number;
  responseMessage: string;
  responseBody: T | null;
}

export interface LoginResponse {
  loginId: string;
  fullName: string;
  token: string;
  groupIds: string[];
}

export interface CardInquiryResponse {
  cardTitle: string;
  cardStatusCode: string;
  cardProdStatus: string;
  pan: string;
  createdOn: string;
  cardTypeName: string;
  expiryDate: string;
  cvv: string;
  cvv2: string;
  pinSet?: boolean;
}

export interface CardLovResponse {
  type: string;
  lov: Record<string, string>;
}

export interface CardLimitValidateResponse {
  availableLimit: number;
  availableTranCount: number;
  maxLimit: number;
}

export interface CardAuthorizeResponse {
  pan: string;
  channelCode: string;
  tranCode: string;
  amount: number;
  maxLimit: number;
  availableLimit: number;
  customizedApplied?: boolean;
  actualRowCreated?: boolean;
}

export interface CardSpendingSummaryResponse {
  accountNumber: string;
  pan: string;
  cardTitle: string;
  expiryDate: string;
  cardStatusName: string;
  maxLimit: number;
  singleTranLimit: number;
  dailyAvailableSpending: number;
  monthlyAvailableSpending: number;
}

export interface SessionCard {
  pan: string;
  relationshipNum: string;
  cardTitle?: string;
  pinSet: boolean;
  accountNumber?: string;
}

export interface SessionUser {
  loginId: string;
  fullName: string;
  token: string;
  relationshipNum: string;
  card?: SessionCard | null;
}

export type LabScreen = 'login' | 'home' | 'request' | 'status' | 'pin' | 'limits';
