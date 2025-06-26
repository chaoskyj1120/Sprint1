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

        Channel channel = channelRepository.getChannelById(messageCreateRequestDTO.getChannelResponseDto().getChannelId());
        User user = userRepository.findUserById(messageCreateRequestDTO.getUserResponseDto().getUserId());
        String contents = messageCreateRequestDTO.getMessageContents();

        if(channel == null) {
            //System.out.println("채널이 null 입니다.");
            return null;
        }
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

        User updateMessageUser = userRepository.findUserById(newMessage.getAuthorId());
        updateMessageUser.addMessage(newMessage);
        userRepository.updateUser(updateMessageUser);

        Channel UpdateMessageChannel = channelRepository.getChannelById(newMessage.getChannelId());
        UpdateMessageChannel.addMessage(newMessage);
        channelRepository.updateChannel(UpdateMessageChannel);

        return new MessageResponseDto(newMessage);
    }

    @Override
    public void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO) {

        User user = userRepository.findUserById(deleteMessageRequestDTO.getUserResponseDto().getUserId());
        Message message = messageRepository.findMessageByMessageId(deleteMessageRequestDTO.getMessageResponseDTO().getMessageId());
        Channel channel = channelRepository.getChannelById(message.getChannelId());


        if (!user.getId().equals(message.getAuthorId())) {
            return;
        }

        if (channel == null) {
            return;
        }

        user.getMessageIds().remove(message.getId());
        channel.getMessageIds().remove(message.getId());


        for (UUID binaryContentId : message.getBinaryContentsIds()) {
            binaryContentsRepository.delete(binaryContentId);
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
        Message message = messageRepository.findMessageByMessageId(messageUpdateRequestDTO.getMessageResponseDTO().getMessageId());
        User user = userRepository.findUserById(messageUpdateRequestDTO.getUserResponseDto().getUserId());
        if (message == null) {
            //System.out.println("메세지가 null 입니다.");
            return null;
        }
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
            binaryContentsRepository.delete(binaryContentId);
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
}
