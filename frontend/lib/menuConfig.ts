import type { AppMenuItem } from '@/types';
import { ROUTES } from '@/lib/constants';

export const menuItems: AppMenuItem[] = [
  {
    label: 'Home',
    items: [{ label: 'Dashboard', icon: 'pi pi-fw pi-home', to: ROUTES.home }],
  },
  {
    label: 'Security',
    items: [
      { label: 'Users', icon: 'pi pi-fw pi-users', to: ROUTES.users },
      { label: 'Roles', icon: 'pi pi-fw pi-shield', to: ROUTES.roles },
      { label: 'Menus', icon: 'pi pi-fw pi-sitemap', to: ROUTES.menus },
      { label: 'Permissions', icon: 'pi pi-fw pi-key', to: ROUTES.permissions },
      { label: 'Audit Logs', icon: 'pi pi-fw pi-history', to: ROUTES.auditLogs },
    ],
  },
  {
    label: 'Operations',
    items: [
      { label: 'Cards', icon: 'pi pi-fw pi-credit-card', to: ROUTES.cards },
      { label: 'Expiry search', icon: 'pi pi-fw pi-calendar', to: ROUTES.cardsExpiry },
      { label: 'Change card type', icon: 'pi pi-fw pi-sync', to: ROUTES.cardsChangeType },
      { label: 'Replacement request', icon: 'pi pi-fw pi-replay', to: ROUTES.cardsReplacement },
      { label: 'Change card status', icon: 'pi pi-fw pi-flag', to: ROUTES.cardsChangeStatus },
      { label: 'Card export', icon: 'pi pi-fw pi-download', to: ROUTES.cardsExport },
    ],
  },
  {
    label: 'Card Production',
    items: [
      { label: 'New Card Request', icon: 'pi pi-fw pi-plus-circle', to: ROUTES.newCardRequest },
      { label: 'Card Requests', icon: 'pi pi-fw pi-list', to: ROUTES.cardRequests },
      { label: 'Search Requests', icon: 'pi pi-fw pi-search', to: ROUTES.cardRequestSearch },
      { label: 'Card Generation', icon: 'pi pi-fw pi-cog', to: ROUTES.cardGeneration },
    ],
  },
  {
    label: 'Housekeeping',
    items: [
      { label: 'Branches', icon: 'pi pi-fw pi-map-marker', to: ROUTES.branches },
      { label: 'Account Statuses', icon: 'pi pi-fw pi-info-circle', to: ROUTES.accountStatuses },
      { label: 'Account Types', icon: 'pi pi-fw pi-list', to: ROUTES.accountTypes },
      { label: 'Policies', icon: 'pi pi-fw pi-file', to: ROUTES.policies },
      { label: 'Password Expressions', icon: 'pi pi-fw pi-lock', to: ROUTES.passwordExpressions },
      { label: 'Response Codes', icon: 'pi pi-fw pi-code', to: ROUTES.responseCodes },
      { label: 'Limit Profiles', icon: 'pi pi-fw pi-wallet', to: ROUTES.limitProfiles },
      { label: 'Products', icon: 'pi pi-fw pi-box', to: ROUTES.products },
      { label: 'Card Types', icon: 'pi pi-fw pi-credit-card', to: ROUTES.cardTypes },
    ],
  },
];
