/* eslint-disable @next/next/no-img-element */

'use client';

import Link from 'next/link';
import { classNames } from 'primereact/utils';
import { Menu } from 'primereact/menu';
import { Toast } from 'primereact/toast';
import type { MenuItem } from 'primereact/menuitem';
import React, { forwardRef, useContext, useImperativeHandle, useMemo, useRef, useState } from 'react';
import { AppTopbarRef } from '@/types';
import { LayoutContext } from './context/layoutcontext';
import { useAuth } from '@/services/auth/AuthContext';
import { ChangePasswordDialog } from '@/components/auth/ChangePasswordDialog';

const AppTopbar = forwardRef<AppTopbarRef>((props, ref) => {
    const { layoutState, onMenuToggle, showProfileSidebar } = useContext(LayoutContext);
    const { user, logout } = useAuth();
    const menubuttonRef = useRef(null);
    const topbarmenuRef = useRef(null);
    const topbarmenubuttonRef = useRef(null);
    const profileMenuRef = useRef<Menu>(null);
    const toast = useRef<Toast>(null);
    const [changePasswordOpen, setChangePasswordOpen] = useState(false);

    useImperativeHandle(ref, () => ({
        menubutton: menubuttonRef.current,
        topbarmenu: topbarmenuRef.current,
        topbarmenubutton: topbarmenubuttonRef.current
    }));

    const displayName = useMemo(() => {
        if (!user) return null;
        const name = user.fullName?.trim();
        return name || user.loginId || null;
    }, [user]);

    const roleLabel = useMemo(() => {
        if (!user?.roles?.length) return null;
        return user.roles.join(', ');
    }, [user]);

    const profileItems = useMemo<MenuItem[]>(
        () => [
            {
                label: 'Change password',
                icon: 'pi pi-key',
                command: () => setChangePasswordOpen(true),
            },
            { separator: true },
            {
                label: 'Logout',
                icon: 'pi pi-sign-out',
                command: () => logout(),
            },
        ],
        [logout]
    );

    return (
        <div className="layout-topbar">
            <Toast ref={toast} />
            <Link href="/" className="layout-topbar-logo">
                <img
                  src="/layout/images/logo-mark-dark.png"
                  height={34}
                  width={34}
                  alt="LasPay"
                />
                <span>
                  <strong>Las</strong>Pay
                </span>
            </Link>

            <button ref={menubuttonRef} type="button" className="p-link layout-menu-button layout-topbar-button" onClick={onMenuToggle}>
                <i className="pi pi-bars" />
            </button>

            <button ref={topbarmenubuttonRef} type="button" className="p-link layout-topbar-menu-button layout-topbar-button" onClick={showProfileSidebar}>
                <i className="pi pi-ellipsis-v" />
            </button>

            <div ref={topbarmenuRef} className={classNames('layout-topbar-menu', { 'layout-topbar-menu-mobile-active': layoutState.profileSidebarVisible })}>
                {displayName && (
                    <>
                        <button
                            type="button"
                            className="layout-topbar-user p-link"
                            title={roleLabel ? `${displayName} · ${roleLabel}` : displayName}
                            aria-haspopup
                            aria-label="Account menu"
                            onClick={(e) => profileMenuRef.current?.toggle(e)}
                        >
                            <div className="layout-topbar-user-avatar" aria-hidden>
                                <i className="pi pi-user" />
                            </div>
                            <div className="layout-topbar-user-text">
                                <span className="layout-topbar-user-name">{displayName}</span>
                                {roleLabel ? (
                                    <span className="layout-topbar-user-role">{roleLabel}</span>
                                ) : user?.loginId && user.fullName?.trim() ? (
                                    <span className="layout-topbar-user-role">{user.loginId}</span>
                                ) : null}
                            </div>
                            <i className="pi pi-angle-down layout-topbar-user-caret" aria-hidden />
                        </button>
                        <Menu model={profileItems} popup ref={profileMenuRef} id="profile-menu" />
                    </>
                )}
            </div>

            <ChangePasswordDialog
                visible={changePasswordOpen}
                onHide={() => setChangePasswordOpen(false)}
                onSuccess={(message) =>
                    toast.current?.show({ severity: 'success', summary: 'Success', detail: message, life: 3000 })
                }
                onError={(message) =>
                    toast.current?.show({ severity: 'error', summary: 'Error', detail: message, life: 5000 })
                }
            />
        </div>
    );
});

AppTopbar.displayName = 'AppTopbar';

export default AppTopbar;
