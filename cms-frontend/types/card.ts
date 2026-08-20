export interface Card {
  /** Alias for cardId when API returns uniform id. Use id ?? cardId for links. */
  id?: number;
  cardId: number;
  panMasked?: string;
  relationshipNum?: string;
  cardTitle?: string;
  expiryDate?: string;
  cardTypeCode?: string;
  cardTypeName?: string;
  cardStatusCode?: string;
  cardStatusName?: string;
  productCode?: string;
  productName?: string;
  branchCode?: string;
  branchName?: string;
  limitProfile?: string;
  activationDate?: string;
  issuedDate?: string;
  createdOn?: string;
  createdBy?: string;
  /** Path to export file when one was generated at card creation. */
  exportFilePath?: string;
}

/** Request body for card update (partial). Prefer id fields when present. */
export interface CardUpdateRequest {
  cardStatusId?: number | null;
  cardStatusCode?: string;
  limitProfileId?: number | null;
  limitProfile?: string | null;
  cardTitle?: string;
}

/** Body for POST /api/cards/expiry-search (maps to Java LocalDate). */
export interface ExpirySearchRequest {
  dateFrom?: string;
  dateTo?: string;
  pan?: string;
}

/** Body for POST /api/cards/{id}/change-card-type */
export interface ChangeCardTypeRequest {
  cardTypeId: number;
}

export interface CardSearchRequest {
  pan?: string;
  relationshipNum?: string;
  cardTypeId?: number | null;
  branchId?: number | null;
  branchCode?: string;
  cardStatusId?: number | null;
  cardStatusCode?: string;
  productCode?: string;
  cardTypeCode?: string;
  dateFrom?: string;
  dateTo?: string;
  page?: number;
  size?: number;
  sort?: string;
  sortDir?: string;
}

export interface CardRequest {
  /** Alias for requestId when API returns uniform id. Use id ?? requestId for links. */
  id?: number;
  requestId: number;
  relationshipNum?: string;
  accountNum?: string;
  cardTitle?: string;
  cardTypeCode?: string;
  cardTypeName?: string;
  productCode?: string;
  productName?: string;
  branchCode?: string;
  branchName?: string;
  supplementaryCount?: number;
  isProcessed?: number;
  progressFlag?: number;
  requestTypeId?: string;
  sourceCardId?: number;
  createdOn?: string;
  createdBy?: string;
}

/** Nested payload when creating a new account with the card request. */
export interface NewAccountRequest {
  accountNum: string;
  accountTitle: string;
  /** Preferred; when null, acctTypeCode is used. */
  accountTypeId?: number | null;
  acctTypeCode?: string;
  accountStatusId?: number | null;
  acctStatusCode?: string;
  branchId?: number | null;
  branchCode?: string;
}

export interface NewCardRequestCreate {
  relationshipNum: string;
  /** Required when newAccount is not set; ignored when newAccount is present. */
  accountNum?: string;
  /** When set, backend creates the account first then uses its accountNum for the card request. */
  newAccount?: NewAccountRequest;
  cardTitle?: string;
  /** Preferred; when null, cardTypeCode is used. */
  cardTypeId?: number | null;
  cardTypeCode?: string;
  productId?: number | null;
  productCode?: string;
  branchId?: number | null;
  branchCode?: string;
  supplementaryCount?: number;
  requestTypeId?: string;
  sourceCardId?: number;
}

export interface CardDropdownsResponse {
  branches?: { id: number; branchCode: string; branchName: string }[];
  cardStatuses?: { id: number; code: string; name: string }[];
  cardProducts?: { id: number; code: string; name: string }[];
  cardTypes?: { id: number; code: string; name: string; productCode?: string }[];
  limitProfiles?: { id: number; code: string; name: string }[];
}

export interface LimitProfile {
  id?: number;
  profileCode: string;
  profileName?: string;
  currencyCode?: string;
  atmDailyAmount?: number;
  atmMonthlyAmount?: number;
  atmYearlyAmount?: number;
  posDailyAmount?: number;
  posMonthlyAmount?: number;
  posYearlyAmount?: number;
  ecommerceDailyAmount?: number;
  ecommerceMonthlyAmount?: number;
  ecommerceYearlyAmount?: number;
  active?: boolean;
}

export interface LimitProfileCreateRequest {
  profileCode: string;
  profileName?: string;
  currencyCode?: string;
  atmDailyAmount?: number;
  atmMonthlyAmount?: number;
  atmYearlyAmount?: number;
  posDailyAmount?: number;
  posMonthlyAmount?: number;
  posYearlyAmount?: number;
  ecommerceDailyAmount?: number;
  ecommerceMonthlyAmount?: number;
  ecommerceYearlyAmount?: number;
  active?: boolean;
}

export interface LimitProfileUpdateRequest {
  profileName?: string;
  currencyCode?: string;
  atmDailyAmount?: number;
  atmMonthlyAmount?: number;
  atmYearlyAmount?: number;
  posDailyAmount?: number;
  posMonthlyAmount?: number;
  posYearlyAmount?: number;
  ecommerceDailyAmount?: number;
  ecommerceMonthlyAmount?: number;
  ecommerceYearlyAmount?: number;
  active?: boolean;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface AccountOption {
  accountNum: string;
  accountTitle?: string;
}

export interface CardAccountLink {
  panMasked?: string;
  accountNum: string;
  accountTitle?: string;
  effectiveFrom?: string;
  effectiveTo?: string;
  isOverallDefault?: number;
  isAcctTypeDefault?: number;
}

export interface CustomerInfo {
  relationshipNum?: string;
  fullName?: string;
  mobilePhone?: string;
  primaryEmail?: string;
  branchCode?: string;
  branchName?: string;
}

export interface CardGenerationResult {
  success: boolean;
  message?: string;
  cardId?: number;
  panMasked?: string;
  /** Path to export file when approve-and-generate is used and export is enabled. */
  exportFilePath?: string;
}

export interface LinkCardAccountRequest {
  pan: string;
  accountNum: string;
  relationshipNum?: string;
  isOverallDefault?: boolean;
  isAcctTypeDefault?: boolean;
}
