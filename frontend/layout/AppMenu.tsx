/* eslint-disable @next/next/no-img-element */

import React from 'react';
import AppMenuitem from './AppMenuitem';
import { MenuProvider } from './context/menucontext';
import { menuItems } from '@/lib/menuConfig';

const AppMenu = () => {
    return (
        <MenuProvider>
            <ul className="layout-menu">
                {menuItems.map((item, i) => {
                    return !item?.seperator ? <AppMenuitem item={item} root={true} index={i} key={item.label} /> : <li className="menu-separator" key={item.label}></li>;
                })}
            </ul>
        </MenuProvider>
    );
};

export default AppMenu;
