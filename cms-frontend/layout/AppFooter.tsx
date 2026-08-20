/* eslint-disable @next/next/no-img-element */

import React from 'react';

const AppFooter = () => {
    return (
        <div className="layout-footer">
            <div className="layout-footer-inner">
                <div className="layout-footer-brand">
                    <img
                        src="/layout/images/logo-mark-dark.png"
                        alt="LasPay"
                        className="layout-footer-brand-mark"
                    />
                    <span className="layout-footer-brand-name">
                        <strong>Las</strong>Pay
                    </span>
                </div>

                <div className="layout-footer-zindigi">
                    <span className="layout-footer-powered-text">Powered by</span>
                    <img
                        src="/layout/images/zindigi-mark.png"
                        alt=""
                        className="layout-footer-zindigi-mark"
                        aria-hidden="true"
                    />
                    <span className="layout-footer-zindigi-name">ZINDIGI</span>
                </div>
            </div>
        </div>
    );
};

export default AppFooter;
