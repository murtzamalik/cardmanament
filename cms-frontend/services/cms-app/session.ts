import type { SessionCard, SessionUser } from './types';

const KEY = 'cms_app_lab_session';
const PIN_SET_KEY = 'cms_app_lab_pin_set_pans';
const CUSTOM_LIMITS_KEY = 'cms_app_lab_custom_limits';

export function saveSession(user: SessionUser): void {
  localStorage.setItem(KEY, JSON.stringify(user));
}

export function getSession(): SessionUser | null {
  const raw = localStorage.getItem(KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as SessionUser;
  } catch {
    return null;
  }
}

export function clearSession(): void {
  localStorage.removeItem(KEY);
}

function readPinSetPans(): Set<string> {
  try {
    const raw = localStorage.getItem(PIN_SET_KEY);
    if (!raw) return new Set();
    const list = JSON.parse(raw) as string[];
    return new Set(Array.isArray(list) ? list.map((p) => p.trim()).filter(Boolean) : []);
  } catch {
    return new Set();
  }
}

function writePinSetPans(pans: Set<string>): void {
  localStorage.setItem(PIN_SET_KEY, JSON.stringify([...pans]));
}

export function markPinSet(pan: string): void {
  const digits = pan.replace(/\s/g, '').trim();
  if (!digits) return;
  const pans = readPinSetPans();
  pans.add(digits);
  writePinSetPans(pans);
}

export function isPinMarkedSet(pan?: string | null): boolean {
  if (!pan) return false;
  const digits = pan.replace(/\s/g, '').trim();
  if (!digits) return false;
  return readPinSetPans().has(digits);
}

export function resolvePinSet(pan?: string | null, apiOrSessionFlag?: boolean | null): boolean {
  return Boolean(apiOrSessionFlag) || isPinMarkedSet(pan);
}

export function updateRelationship(relationshipNum: string): void {
  const s = getSession();
  if (!s) return;
  saveSession({ ...s, relationshipNum });
}

export function saveCardContext(card: SessionCard | null): void {
  const s = getSession();
  if (!s) return;
  saveSession({
    ...s,
    relationshipNum: card?.relationshipNum || s.relationshipNum,
    card,
  });
}

export function getCardContext(): SessionCard | null {
  return getSession()?.card ?? null;
}

export function maskPan(pan?: string | null): string {
  if (!pan) return '******';
  const raw = String(pan).replace(/\s/g, '');
  if (/^\d{6}\*+\d{4}$/.test(raw)) return raw;
  const digits = raw.replace(/\D/g, '');
  if (digits.length === 0) return '******';
  if (digits.length <= 4) return '*'.repeat(Math.max(6, digits.length)) + digits;
  if (digits.length < 10) {
    const last4 = digits.slice(-4);
    const front = digits.slice(0, Math.max(0, digits.length - 4));
    return `${front}${'*'.repeat(6)}${last4}`;
  }
  const first6 = digits.slice(0, 6);
  const last4 = digits.slice(-4);
  const middle = Math.max(6, digits.length - 10);
  return `${first6}${'*'.repeat(middle)}${last4}`;
}

export function formatExpiryMonthYear(expiry?: string | null): string {
  if (!expiry) return '—';
  const raw = String(expiry).trim();
  if (!raw) return '—';

  const slash = raw.match(/^(\d{1,2})\s*\/\s*(\d{2}|\d{4})$/);
  if (slash) {
    const mm = slash[1].padStart(2, '0');
    const yy = slash[2].length === 2 ? `20${slash[2]}` : slash[2];
    return `${mm}/${yy}`;
  }

  const iso = raw.match(/^(\d{4})-(\d{2})/);
  if (iso) return `${iso[2]}/${iso[1]}`;

  const d = new Date(raw);
  if (!Number.isNaN(d.getTime())) {
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    return `${mm}/${d.getFullYear()}`;
  }

  return raw;
}

export function formatMoney(n?: number | null): string {
  if (n == null || Number.isNaN(n)) return '—';
  return new Intl.NumberFormat('en-US', {
    maximumFractionDigits: 2,
    minimumFractionDigits: 0,
  }).format(n);
}

type CustomLimitsMap = Record<string, Record<string, number>>;

function readCustomLimits(): CustomLimitsMap {
  try {
    const raw = localStorage.getItem(CUSTOM_LIMITS_KEY);
    if (!raw) return {};
    const parsed = JSON.parse(raw) as CustomLimitsMap;
    return parsed && typeof parsed === 'object' ? parsed : {};
  } catch {
    return {};
  }
}

function writeCustomLimits(map: CustomLimitsMap): void {
  localStorage.setItem(CUSTOM_LIMITS_KEY, JSON.stringify(map));
}

export function getCustomLimit(pan: string, channelCode: number): number | null {
  const digits = pan.replace(/\D/g, '').trim();
  if (!digits) return null;
  const value = readCustomLimits()[digits]?.[String(channelCode)];
  return typeof value === 'number' && Number.isFinite(value) ? value : null;
}

export function setCustomLimit(pan: string, channelCode: number, amount: number): void {
  const digits = pan.replace(/\D/g, '').trim();
  if (!digits) return;
  const map = readCustomLimits();
  if (!map[digits]) map[digits] = {};
  map[digits][String(channelCode)] = amount;
  writeCustomLimits(map);
}
