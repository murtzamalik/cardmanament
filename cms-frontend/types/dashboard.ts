import type { CardRequest } from '@/types/card';

export interface DashboardExpiringCard {
  cardId: number;
  panLast4?: string;
  relationshipNum?: string;
  cardTitle?: string;
  expiryDate?: string;
  cardStatusCode?: string;
  branchCode?: string;
}

export interface DashboardSummary {
  pendingApproval: number;
  openRequests: number;
  issuedToday: number;
  expiringIn30Days: number;
  hotCards: number;
  requestsByStatus: Record<string, number>;
  cardsByStatus: Record<string, number>;
  checkerQueue: CardRequest[];
  makerQueue: CardRequest[];
  expiringSoon: DashboardExpiringCard[];
}
