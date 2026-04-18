package com.assistacrise.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.assistacrise.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MessageChatDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MessageChatDTO.class);
        MessageChatDTO messageChatDTO1 = new MessageChatDTO();
        messageChatDTO1.setId(1L);
        MessageChatDTO messageChatDTO2 = new MessageChatDTO();
        assertThat(messageChatDTO1).isNotEqualTo(messageChatDTO2);
        messageChatDTO2.setId(messageChatDTO1.getId());
        assertThat(messageChatDTO1).isEqualTo(messageChatDTO2);
        messageChatDTO2.setId(2L);
        assertThat(messageChatDTO1).isNotEqualTo(messageChatDTO2);
        messageChatDTO1.setId(null);
        assertThat(messageChatDTO1).isNotEqualTo(messageChatDTO2);
    }
}
