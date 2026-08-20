'use client';

import { useEffect, useMemo, useState } from 'react';
import { useAuth } from '@/services/auth/AuthContext';
import { ROUTES, config } from '@/lib/constants';
import { getSession } from '@/services/cms-app/session';
import type { LabScreen } from '@/services/cms-app/types';
import { LabNavContext } from './nav-context';
import { AppNav } from './ui';
import { LoginScreen } from './LoginScreen';
import { HomeScreen } from './HomeScreen';
import { RequestScreen } from './RequestScreen';
import { StatusScreen } from './StatusScreen';
import { PinScreen } from './PinScreen';
import { LimitsScreen } from './LimitsScreen';
import styles from './CmsAppLab.module.scss';

export function CmsAppLab() {
  const { isAuthenticated, initialized } = useAuth();
  const [screen, setScreen] = useState<LabScreen>('login');
  const [ready, setReady] = useState(false);

  useEffect(() => {
    if (!initialized) return;
    if (!isAuthenticated) {
      window.location.replace(ROUTES.login);
      return;
    }
    setScreen(getSession()?.token ? 'home' : 'login');
    setReady(true);
  }, [initialized, isAuthenticated]);

  const nav = useMemo(() => ({ screen, go: setScreen }), [screen]);

  if (!initialized || !isAuthenticated || !ready) {
    return (
      <div className={styles.gate}>
        <i className="pi pi-spin pi-spinner" />
        <p>Checking access…</p>
      </div>
    );
  }

  return (
    <LabNavContext.Provider value={nav}>
      <div className={styles.wrap}>
        {screen === 'login' ? (
          <LoginScreen />
        ) : (
          <>
            <header className="lab-topbar">
              <div className="lab-topbar-inner">
                <div className="brand-mark">CMS</div>
                <AppNav />
                <p className="lab-api">{config.cmsAppBaseUrl}</p>
              </div>
            </header>
            <div className="lab-main">
              <div className="lab-content">
                {screen === 'home' ? <HomeScreen /> : null}
                {screen === 'request' ? <RequestScreen /> : null}
                {screen === 'status' ? <StatusScreen /> : null}
                {screen === 'pin' ? <PinScreen /> : null}
                {screen === 'limits' ? <LimitsScreen /> : null}
              </div>
            </div>
          </>
        )}
      </div>
    </LabNavContext.Provider>
  );
}
