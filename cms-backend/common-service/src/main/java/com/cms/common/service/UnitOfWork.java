package com.cms.common.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional facade: use @Transactional on service methods; inject repositories directly.
 * This bean exists for compatibility with legacy "UnitOfWork.GetRepository<T>" pattern;
 * in Spring, prefer injecting JpaRepository beans directly in managers.
 */
@Component
@Scope("prototype")
public class UnitOfWork {

    // In Spring, repositories are injected where needed. This class can hold optional
    // shared state per request (e.g. audit context) if required.
}
