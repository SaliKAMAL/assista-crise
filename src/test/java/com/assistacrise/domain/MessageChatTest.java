package com.assistacrise.domain;

import static com.assistacrise.domain.DemandeTestSamples.*;
import static com.assistacrise.domain.MessageChatTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.assistacrise.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MessageChatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MessageChat.class);
        MessageChat messageChat1 = getMessageChatSample1();
        MessageChat messageChat2 = new MessageChat();
        assertThat(messageChat1).isNotEqualTo(messageChat2);

        messageChat2.setId(messageChat1.getId());
        assertThat(messageChat1).isEqualTo(messageChat2);

        messageChat2 = getMessageChatSample2();
        assertThat(messageChat1).isNotEqualTo(messageChat2);
    }

    @Test
    void demandeTest() {
        MessageChat messageChat = getMessageChatRandomSampleGenerator();
        Demande demandeBack = getDemandeRandomSampleGenerator();

        messageChat.setDemande(demandeBack);
        assertThat(messageChat.getDemande()).isEqualTo(demandeBack);

        messageChat.demande(null);
        assertThat(messageChat.getDemande()).isNull();
    }
}
