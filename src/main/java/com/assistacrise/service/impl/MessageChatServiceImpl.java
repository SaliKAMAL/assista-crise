package com.assistacrise.service.impl;

import com.assistacrise.domain.MessageChat;
import com.assistacrise.repository.MessageChatRepository;
import com.assistacrise.service.MessageChatService;
import com.assistacrise.service.dto.MessageChatDTO;
import com.assistacrise.service.mapper.MessageChatMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.assistacrise.domain.MessageChat}.
 */
@Service
@Transactional
public class MessageChatServiceImpl implements MessageChatService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageChatServiceImpl.class);

    private final MessageChatRepository messageChatRepository;

    private final MessageChatMapper messageChatMapper;

    public MessageChatServiceImpl(MessageChatRepository messageChatRepository, MessageChatMapper messageChatMapper) {
        this.messageChatRepository = messageChatRepository;
        this.messageChatMapper = messageChatMapper;
    }

    @Override
    public MessageChatDTO save(MessageChatDTO messageChatDTO) {
        LOG.debug("Request to save MessageChat : {}", messageChatDTO);
        MessageChat messageChat = messageChatMapper.toEntity(messageChatDTO);
        messageChat = messageChatRepository.save(messageChat);
        return messageChatMapper.toDto(messageChat);
    }

    @Override
    public MessageChatDTO update(MessageChatDTO messageChatDTO) {
        LOG.debug("Request to update MessageChat : {}", messageChatDTO);
        MessageChat messageChat = messageChatMapper.toEntity(messageChatDTO);
        messageChat = messageChatRepository.save(messageChat);
        return messageChatMapper.toDto(messageChat);
    }

    @Override
    public Optional<MessageChatDTO> partialUpdate(MessageChatDTO messageChatDTO) {
        LOG.debug("Request to partially update MessageChat : {}", messageChatDTO);

        return messageChatRepository
            .findById(messageChatDTO.getId())
            .map(existingMessageChat -> {
                messageChatMapper.partialUpdate(existingMessageChat, messageChatDTO);

                return existingMessageChat;
            })
            .map(messageChatRepository::save)
            .map(messageChatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageChatDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all MessageChats");
        return messageChatRepository.findAll(pageable).map(messageChatMapper::toDto);
    }

    public Page<MessageChatDTO> findAllWithEagerRelationships(Pageable pageable) {
        return messageChatRepository.findAllWithEagerRelationships(pageable).map(messageChatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MessageChatDTO> findOne(Long id) {
        LOG.debug("Request to get MessageChat : {}", id);
        return messageChatRepository.findOneWithEagerRelationships(id).map(messageChatMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete MessageChat : {}", id);
        messageChatRepository.deleteById(id);
    }
}
