/* eslint-disable @next/next/no-img-element */

import React from 'react';
import AppMenuitem from './AppMenuitem';
import { MenuProvider } from './context/menucontext';
import { menuItems } from '@/lib/menuConfig';
import { withTrailingSlash } from '@/lib/constants';
import { useAuth } from '@/services/auth/AuthContext';
import type { AppMenuItem } from '@/types';
import type { MenuResponse } from '@/types/menu';

/** Ordered category rules — first match wins */
const CATEGORY_RULES: { prefix: string; label: string }[] = [
    { prefix: '/security', label: 'Security' },
    { prefix: '/operations', label: 'Operations' },
    { prefix: '/card-production', label: 'Card Production' },
    { prefix: '/housekeeping', label: 'Housekeeping' },
];

function normalizeIcon(icon?: string | null): string {
    if (!icon || !icon.trim()) return 'pi pi-fw pi-circle';
    const value = icon.trim();
    if (value.startsWith('pi ')) return value;
    if (value.startsWith('pi-')) return `pi pi-fw ${value}`;
    return `pi pi-fw ${value}`;
}

function isCategoryOnly(path?: string | null): boolean {
    if (!path || !path.trim()) return true;
    const p = path.trim();
    return p === '#' || p.startsWith('#');
}

/** Flatten tree from API into a single list of navigable leaves */
function flattenMenus(menus: MenuResponse[]): MenuResponse[] {
    const out: MenuResponse[] = [];
    const walk = (nodes: MenuResponse[]) => {
        for (const n of nodes) {
            if (Array.isArray(n.children) && n.children.length > 0) {
                walk(n.children);
            } else if (!isCategoryOnly(n.menuPath)) {
                out.push(n);
            }
        }
    };
    walk(menus);
    return out;
}

function categoryForPath(path?: string | null): string {
    if (!path || !path.trim() || path.trim() === '/') return 'Home';
    const p = path.trim().replace(/\/+$/, '') || '/';
    if (p === '/') return 'Home';
    for (const rule of CATEGORY_RULES) {
        if (p === rule.prefix || p.startsWith(rule.prefix + '/')) {
            return rule.label;
        }
    }
    return 'Other';
}

function toAppMenuItems(menus: MenuResponse[]): AppMenuItem[] {
    const leaves = flattenMenus(menus);

    const order = ['Home', ...CATEGORY_RULES.map((r) => r.label), 'Other'];
    const buckets = new Map<string, AppMenuItem[]>();

    for (const menu of leaves) {
        const cat = categoryForPath(menu.menuPath);
        const item: AppMenuItem = {
            label: menu.menuName,
            icon: normalizeIcon(menu.menuIcon),
            to: withTrailingSlash(menu.menuPath),
        };
        const list = buckets.get(cat) ?? [];
        list.push(item);
        buckets.set(cat, list);
    }

    const result: AppMenuItem[] = [];
    for (const label of order) {
        const items = buckets.get(label);
        if (items && items.length > 0) {
            result.push({ label, items });
        }
    }
    Array.from(buckets.entries()).forEach(([label, items]) => {
        if (!order.includes(label) && items.length > 0) {
            result.push({ label, items });
        }
    });
    return result;
}

const AppMenu = () => {
    const { user } = useAuth();
    const dynamicItems = user?.menus && user.menus.length > 0 ? toAppMenuItems(user.menus) : menuItems;
    return (
        <MenuProvider>
            <ul className="layout-menu">
                {dynamicItems.map((item, i) => {
                    return !item?.seperator ? <AppMenuitem item={item} root={true} index={i} key={item.label} /> : <li className="menu-separator" key={item.label}></li>;
                })}
            </ul>
        </MenuProvider>
    );
};

export default AppMenu;
