package com.assistacrise.service.mapper;

import static com.assistacrise.domain.MessageChatAsserts.*;
import static com.assistacrise.domain.MessageChatTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageChatMapperTest {

    private MessageChatMapper messageChatMapper;

    @BeforeEach
    void setUp() {
        messageChatMapper = new MessageChatMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMessageChatSample1();
        var actual = messageChatMapper.toEntity(messageChatMapper.toDto(expected));
        assertMessageChatAllPropertiesEquals(expected, actual);
    }
}
