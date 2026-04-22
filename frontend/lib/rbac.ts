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

function flattenPaths(menus: MenuResponse[]): string[] {
  const out: string[] = [];
  const walk = (nodes: MenuResponse[]) => {
    for (const n of nodes) {
      if (n.menuPath) out.push(n.menuPath);
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
  return allowedPaths.some((allowed) => allowed !== '/' && target.startsWith(`${allowed}/`));
}
