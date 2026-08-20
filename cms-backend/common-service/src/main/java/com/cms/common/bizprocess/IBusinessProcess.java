package com.cms.common.bizprocess;

import com.cms.common.model.IProcessMessage;

/**
 * Contract for all Manager services (Strategy pattern). Core resolves by ClassName and invokes execute(methodName, ...).
 */
public interface IBusinessProcess {

    /**
     * Execute the given method (e.g. CreateAccountStatus, UpdateAccountStatus) with the request message.
     * Result is set on responseMessage (success/failure, CMSResponse).
     */
    boolean execute(String methodName, IProcessMessage requestMessage, IProcessMessage responseMessage);
}
