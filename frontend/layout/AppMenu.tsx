/* eslint-disable @next/next/no-img-element */

import React from 'react';
import AppMenuitem from './AppMenuitem';
import { MenuProvider } from './context/menucontext';
import { menuItems } from '@/lib/menuConfig';
import { useAuth } from '@/services/auth/AuthContext';
import type { AppMenuItem } from '@/types';
import type { MenuResponse } from '@/types/menu';

function toAppMenuItems(menus: MenuResponse[]): AppMenuItem[] {
    const grouped = menus.map((menu) => ({
        label: menu.menuName,
        icon: menu.menuIcon || 'pi pi-fw pi-circle',
        to: menu.menuPath,
        items: menu.children && menu.children.length ? toAppMenuItems(menu.children) : undefined,
    }));
    const rootsWithChildren = grouped.filter((m) => m.items && m.items.length > 0);
    const rootLeaves = grouped.filter((m) => !m.items || m.items.length === 0);
    if (rootLeaves.length > 0) {
        rootsWithChildren.unshift({
            label: 'Menu',
            items: rootLeaves,
        });
    }
    return rootsWithChildren;
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
