package com.cms.business.manager;

import com.cms.common.model.BizMessage;
import com.cms.common.model.IProcessMessage;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Skeleton unit test for AccountStatusManager. For integration test use @SpringBootTest with test profile and DB.
 */
class AccountStatusManagerTest {

    @Test
    void bizMessage_acceptsMsgData() {
        IProcessMessage req = new BizMessage();
        req.setMsgData(Map.of("ACCT_STATUS_NAME", "Test", "DESCRIPTION", "Desc"));
        assertThat(req.getMsgData()).containsEntry("ACCT_STATUS_NAME", "Test");
    }
}
