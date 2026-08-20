import type { MenuResponse } from '@/types/menu';

const MENUS_KEY = 'cms_menus';

export function setStoredMenus(menus: MenuResponse[]): void {
  if (typeof window === 'undefined') return;
  localStorage.setItem(MENUS_KEY, JSON.stringify(Array.isArray(menus) ? menus : []));
}

export function clearStoredMenus(): void {
  if (typeof window === 'undefined') return;
  localStorage.removeItem(MENUS_KEY);
}

export function getStoredMenus(): MenuResponse[] {
  if (typeof window === 'undefined') return [];
  const raw = localStorage.getItem(MENUS_KEY);
  if (!raw) return [];
  try {
    const parsed = JSON.parse(raw) as MenuResponse[];
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

function isNavigablePath(path?: string | null): boolean {
  if (!path || !path.trim()) return false;
  const p = path.trim();
  return p !== '#' && !p.startsWith('#');
}

function flattenPaths(menus: MenuResponse[]): string[] {
  const out: string[] = [];
  const walk = (nodes: MenuResponse[]) => {
    for (const n of nodes) {
      if (isNavigablePath(n.menuPath)) out.push(n.menuPath!);
      if (Array.isArray(n.children) && n.children.length > 0) walk(n.children);
    }
  };
  walk(menus);
  return out;
}

function normalizePath(path: string): string {
  if (!path) return '/';
  const p = path.trim();
  if (p === '/') return '/';
  return p.replace(/\/+$/, '');
}

export function hasMenuAccess(path: string, menus?: MenuResponse[]): boolean {
  const target = normalizePath(path);
  const source = menus ?? getStoredMenus();
  const allowedPaths = flattenPaths(source).map(normalizePath);
  if (allowedPaths.includes(target)) return true;
  // Child of an allowed menu (e.g. /operations/cards/detail under /operations/cards)
  if (allowedPaths.some((allowed) => allowed !== '/' && target.startsWith(`${allowed}/`))) {
    return true;
  }
  // Section root when user has any child under it (e.g. /operations with /operations/cards)
  if (target !== '/' && allowedPaths.some((allowed) => allowed.startsWith(`${target}/`))) {
    return true;
  }
  return false;
}
