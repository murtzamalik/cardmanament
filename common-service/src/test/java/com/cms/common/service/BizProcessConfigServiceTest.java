package com.cms.common.service;

import com.cms.common.bizprocess.BizProcessModel;
import com.cms.common.enums.MessageType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Skeleton unit test for BizProcessConfigService. Integration test with repository can be added when test DB is available.
 */
class BizProcessConfigServiceTest {

    @Test
    void bizProcessModel_holdsStateTable() {
        BizProcessModel model = new BizProcessModel();
        model.setChannelId(1L);
        model.setMessageType(MessageType.Prepaid);
        assertThat(model.getStateTable()).isEmpty();
    }
}
