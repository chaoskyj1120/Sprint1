package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentsRepository binaryContentsRepository;

    @Override
    public MessageResponseDto createMessage(MessageCreateRequestDto messageCreateRequestDTO) {

        Optional<User> userFromFile = userRepository.findUserById(messageCreateRequestDTO.getUserResponseDto().getUserId());
        Optional<Channel> channelFromFile = channelRepository.findChannelByChannelId(messageCreateRequestDTO.getChannelResponseDto().getChannelId());

        optionalUserIsEmpty(userFromFile);
        optionalChannelIsEmpty(channelFromFile);

        String contents = messageCreateRequestDTO.getMessageContents();

        User user = userFromFile.get();
        Channel channel = channelFromFile.get();

        if(user.getStatus().equals(UserActivationState.DEACTIVE)) {
            //System.out.printf("'%s' 비활성 상태라 메세지를 작성할 수 없습니다.", user.getUserName());
            return null;
        }

        Message newMessage = new Message(user, channel, contents);
        newMessage.registerMessageToUserAndChannel(user, channel);

        List<BinaryContents> binaryContentsList;

        if (messageCreateRequestDTO.getExtraContentsFilePath() != null){
            // 메세지 첨부 파일이 있다는 의미
            // 첨부 파일 바이트화 -> 바이너리 컨텐츠 추가 -> 메세지의 바이너리컨텐츠 ids에 해당 바이너리 컨튼체으 객체의 아이디 추가
            // 유저 프로필 참고하기

            for (String filePath : messageCreateRequestDTO.getExtraContentsFilePath()) {
                byte[] extraFileBytes = null;
                try (FileInputStream fis = new FileInputStream(filePath)) {
                    extraFileBytes = fis.readAllBytes();
                } catch (IOException e) {
                    throw new RuntimeException("바이트화가 불가능합니다: " + e.getMessage(), e);
                }

                BinaryContents newBinaryContent = new BinaryContents(filePath, BinaryContentType.USER_PROFILE_IMAGE, extraFileBytes);
                newMessage.addBinaryContentsId(newBinaryContent.getId());
                newBinaryContent.setReferenceId(newMessage.getId());

                binaryContentsRepository.createBinaryContents(newBinaryContent);
            }
        }

        //binaryContentsList = binaryContentsRepository.loadBinaryContents();
        //System.out.println(binaryContentsList.size());
        messageRepository.createMessage(newMessage);

        Optional<User> updateMessageUserFromFile = userRepository.findUserById(newMessage.getAuthorId());
        optionalUserIsEmpty(updateMessageUserFromFile);
        User updateMessageUser = updateMessageUserFromFile.get();
        updateMessageUser.addMessage(newMessage);
        userRepository.updateUser(updateMessageUser);

        Optional<Channel> updateMessageChannelFromFile = channelRepository.findChannelByChannelId(newMessage.getChannelId());
        optionalChannelIsEmpty(updateMessageChannelFromFile);
        Channel updateMessageChannel = updateMessageChannelFromFile.get();
        updateMessageChannel.addMessage(newMessage);
        channelRepository.updateChannel(updateMessageChannel);

        return new MessageResponseDto(newMessage);
    }

    @Override
    public void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO) {

        Optional<User> userFromFile = userRepository.findUserById(deleteMessageRequestDTO.getUserResponseDto().getUserId());
        optionalUserIsEmpty(userFromFile);
        User user = userFromFile.get();

        Optional<Message> messageFromFile = messageRepository.findMessageByMessageId(deleteMessageRequestDTO.getMessageResponseDTO().getMessageId());
        optionalMessageIsEmpty(messageFromFile);
        Message message = messageFromFile.get();

        Optional<Channel> channelFromFile = channelRepository.findChannelByChannelId(message.getChannelId());
        optionalChannelIsEmpty(channelFromFile);
        Channel channel = channelFromFile.get();

        user.getMessageIds().remove(message.getId());
        channel.getMessageIds().remove(message.getId());

        for (UUID binaryContentId : message.getBinaryContentsIds()) {
            binaryContentsRepository.deleteBinaryContensByBinaryContentsId(binaryContentId);
        }

        userRepository.updateUser(user);
        channelRepository.updateChannel(channel);

        messageRepository.deleteMessageByMessageId(message.getId());
    }

    @Override
    public List<MessageResponseDto> findAllMessage(){
        List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();

        for (Message message : messageRepository.loadMessages()){
            messageResponseDtoList.add(new MessageResponseDto(message));
        }
        return messageResponseDtoList;
    }

    @Override
    public List<MessageResponseDto> findMessagesByChannelId(UUID channelId){
        List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();
        for (Message message : messageRepository.findMessagesByChannelId(channelId)){
            messageResponseDtoList.add(new MessageResponseDto(message));
        }
        return messageResponseDtoList;
    }


    @Override
    public MessageResponseDto updateMessage(MessageUpdateRequestDto messageUpdateRequestDTO) {
        Optional<Message> messageFromFile = messageRepository.findMessageByMessageId(messageUpdateRequestDTO.getMessageResponseDTO().getMessageId());
        optionalMessageIsEmpty(messageFromFile);
        Message message = messageFromFile.get();

        Optional<User> userFromFile = userRepository.findUserById(messageUpdateRequestDTO.getUserResponseDto().getUserId());
        optionalUserIsEmpty(userFromFile);
        User user = userFromFile.get();

        if (user.getStatus().equals(UserActivationState.DEACTIVE)) {
            //System.out.printf("'%s' 비활성 상태라 메세지를 업데이트할 수 없습니다.", user.getUserName());
            return null;
        }
        if (!user.getId().equals(message.getAuthorId())) {
            //System.out.printf("'%s'는 '%s' 메시지의 주인이 아닙니다. 따라서 해당 메시지를 '%s'로 바꾸는 것은 불가능합니다.", user.getUserName(), message.getMessageContents(), newContents);
            return null ;
        }

        message.setMessageContents(messageUpdateRequestDTO.getNewContent());

        for (UUID binaryContentId : message.getBinaryContentsIds()) {
            binaryContentsRepository.deleteBinaryContensByBinaryContentsId(binaryContentId);
        }
        message.clearBinaryContentsId();

        if (messageUpdateRequestDTO.getExtraContentsFilePath() != null){
            // 메세지 첨부 파일이 있다는 의미
            // 첨부 파일 바이트화 -> 바이너리 컨텐츠 추가 -> 메세지의 바이너리컨텐츠 ids에 해당 바이너리 컨튼체으 객체의 아이디 추가
            // 유저 프로필 참고하기

            for (String filePath : messageUpdateRequestDTO.getExtraContentsFilePath()) {
                byte[] extraFileBytes = null;
                try (FileInputStream fis = new FileInputStream(filePath)) {
                    extraFileBytes = fis.readAllBytes();
                } catch (IOException e) {
                    throw new RuntimeException("바이트화가 불가능합니다: " + e.getMessage(), e);
                }

                BinaryContents extraFile = new BinaryContents(filePath, BinaryContentType.USER_PROFILE_IMAGE, extraFileBytes);
                message.addBinaryContentsId(extraFile.getId());

                binaryContentsRepository.createBinaryContents(extraFile);
            }
        }

        messageRepository.updateMessage(message);
        return new MessageResponseDto(message);
    }

    private void optionalUserIsEmpty(Optional<User> user) {
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User 정보가 없습니다.");
        }
    }

    private void optionalChannelIsEmpty(Optional<Channel> channel) {
        if (channel.isEmpty()) {
            throw new IllegalArgumentException("Channel 정보가 없습니다.");
        }
    }

    private void optionalMessageIsEmpty(Optional<Message> message) {
        if (message.isEmpty()) {
            throw new IllegalArgumentException("Message 정보가 없습니다.");
        }
    }

    private void optionalChannelIsPresent(Optional<Channel> channel) {
        if (channel.isPresent()) {
            throw new IllegalArgumentException("Channel 정보가 중복됩니다.");
        }
    }
}