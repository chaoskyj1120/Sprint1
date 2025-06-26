package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel_service_dto.*;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    // TODO 메소드 명, 필드명 한 번 고민하기
    // 모든 반환 타입을 DTO로
    // 컨트롤러에서 넘어오는 파라미터는 모두 DTO로
    // Repository에서 전체를 불러와서 조회하지 않도록 필요한 경우가 아니면


    @Override
    public ChannelResponseDto createPublicChannel(CreateChannelRequestDto createChannelRequestDTO) {

        Optional<User> user = userRepository.findUserById(createChannelRequestDTO.getHostUserId());
        optionalUserIsEmpty(user);

        Optional<Channel> duplicateChannel = channelRepository.findChannelByChannelName(createChannelRequestDTO.getChannelName());
        optionalChannelIsPresent(duplicateChannel);

        Channel channel = new Channel(createChannelRequestDTO.getHostUserId(),
                createChannelRequestDTO.getChannelName(),
                createChannelRequestDTO.getDescription());

        Optional<User> hostUserFromFile = userRepository.findUserById(createChannelRequestDTO.getHostUserId());
        optionalUserIsEmpty(hostUserFromFile);

        User hostUser = hostUserFromFile.get();

        channel.addUser(hostUser);
        hostUser.addChannel(channel);

        channelRepository.createChannel(channel);
        userRepository.updateUser(hostUser);

        //createOrUpdateReadStatus(hostUser, channel);

        List<Message> messages = messageRepository.findMessagesByChannelId(channel.getId());
        return new ChannelResponseDto(channel, messages.get(messages.size()-1).getId());
    }

    @Override
    public ChannelResponseDto createPrivateChannel(CreateChannelRequestDto createChannelRequestDTO, UserResponseDto enterUserResponseDto) {

        Optional<User> hostUserFromFile = userRepository.findUserById(createChannelRequestDTO.getHostUserId());
        optionalUserIsEmpty(hostUserFromFile);
        User hostUser = hostUserFromFile.get();


        Optional<User> enterUserFromFile = userRepository.findUserById(enterUserResponseDto.getUserId());
        optionalUserIsEmpty(enterUserFromFile);
        User enterUser = enterUserFromFile.get();

        List<Channel> channelsInHostUser = channelRepository.findChannelsByUserId(hostUser.getId());

        Optional<Channel> duplicateChannel = channelsInHostUser.stream().filter(ch -> ch.getUserIds().contains(enterUserResponseDto.getUserId())).findFirst();
        optionalChannelIsPresent(duplicateChannel);

        Channel channel = new Channel(hostUser.getId());
        channel.addUser(hostUser);
        channel.addUser(enterUser);

        hostUser.addChannel(channel);
        enterUser.addChannel(channel);

        channelRepository.createChannel(channel);
        userRepository.updateUser(hostUser);
        userRepository.updateUser(enterUser);

        //createOrUpdateReadStatus(hostUser, channel);
        //createOrUpdateReadStatus(enterUser, channel);

        List<Message> messages = messageRepository.findMessagesByChannelId(channel.getId());
        return new ChannelResponseDto(channel, messages.get(messages.size()-1).getId());
    }

    @Override
    public void updateChannelName(ChannelNameUpdateRequestDto channelNameUpdateRequestDTO) {
        // 1. 채널의 호스티인지 검증
        // 2. 변경된 채넝을 넘겨주기
        Optional<User> userFromFile = userRepository.findUserById(channelNameUpdateRequestDTO.getUserResponseDto().getUserId());
        Optional<Channel> channelFromFile = channelRepository.findChannelByChannelId(channelNameUpdateRequestDTO.getChannelResponseDto().getChannelId());

        optionalUserIsEmpty(userFromFile);
        optionalChannelIsEmpty(channelFromFile);

        User user = userFromFile.get();
        Channel channel = channelFromFile.get();

        if (!user.equalsId(channel.getHostUserId())){
            System.out.println("해당 채널의 호스트가 아니라 권한이 없습니다.\n"); //테스트용 나중에 throw로 바꾸기
            return;
        }

        Optional<Channel> duplicateNewName = channelRepository.findChannelByChannelName(channelNameUpdateRequestDTO.getChannelNewName());
        optionalChannelIsPresent(duplicateNewName);

        channel.setChannelName(channelNameUpdateRequestDTO.getChannelNewName());
        channelRepository.updateChannel(channel);
    }

    @Override
    public void deleteChannel(DeleteChannelRequestDto deleteChannelRequestDTO) {
        Optional<User> userFromFile = userRepository.findUserById(deleteChannelRequestDTO.getUserResponseDto().getUserId());
        Optional<Channel> channelFromFile = channelRepository.findChannelByChannelId(deleteChannelRequestDTO.getChannelResponseDto().getChannelId());

        optionalUserIsEmpty(userFromFile);
        optionalChannelIsEmpty(channelFromFile);

        User user = userFromFile.get();
        Channel channel = channelFromFile.get();

        if (user.getStatus() == UserActivationState.DEACTIVE){
            System.out.println("유저 상태가 비활성입니다.\n");
            return;
        }

        if (!channel.getHostUserId().equals(user.getId())) {
            System.out.println("채널을 삭제할 권한이 없습니다.");
            return;
        }

        // 채널에서 유저 제거 같은 비즈니스 로직 수행
        List<User> users = userRepository.loadUsers();
        for (User u : users) {
            u.removeChannel(channel);
            u.getMessageIds().removeIf(messageId -> channel.getMessageIds().contains(messageId));
        }


        readStatusRepository.deleteReadStatusByChannelId(deleteChannelRequestDTO.getChannelResponseDto().getChannelId());
        messageRepository.deleteMessagesByChannelId(channel.getId());
        userRepository.saveUsers(users);
        channelRepository.deleteChannel(channel);
    }

    @Override
    public void addUserToChannel(AddUserToChannelRequestDto addUserToChannelRequestDTO) {
        Optional<User> userFromFile = userRepository.findUserById(addUserToChannelRequestDTO.getUserResponseDto().getUserId());
        Optional<Channel> channelFromFile = channelRepository.findChannelByChannelId(addUserToChannelRequestDTO.getChannelResponseDto().getChannelId());

        optionalUserIsEmpty(userFromFile);
        optionalChannelIsEmpty(channelFromFile);
        User user = userFromFile.get();
        Channel channel = channelFromFile.get();


        if (user.getStatus() == UserActivationState.DEACTIVE){
            System.out.println("유저 상태가 비활성입니다.");
            return;
        }

        user.addChannel(channel);
        userRepository.updateUser(user);

        channel.addUser(user);
        channelRepository.updateChannel(channel);
    }

    @Override
    public void leaveUserFromChannel(LeaveUserFromChannelRequestDto leaveUserFromChannelRequestDTO) {

        Optional<User> userFromFile = userRepository.findUserById(leaveUserFromChannelRequestDTO.getUserResponseDto().getUserId());
        Optional<Channel> channelFromFile = channelRepository.findChannelByChannelId(leaveUserFromChannelRequestDTO.getChannelResponseDto().getChannelId());

        optionalUserIsEmpty(userFromFile);
        optionalChannelIsEmpty(channelFromFile);

        User user = userFromFile.get();
        Channel channel = channelFromFile.get();

        user.removeChannel(channel);
        userRepository.updateUser(user);
        // 유저 찾기 -> 해당 채널 삭제 -> 유저 업데이트

        channel.removeUser(user);
        channelRepository.updateChannel(channel);
    }

    @Override
    public void updateHostUser(ChannelHostUserUpdateRequestDto channelHostUserUpdateRequestDTO) {

        Optional<User> oldHostUserFromFile = userRepository.findUserById(channelHostUserUpdateRequestDTO.getOldHostUserResponseDto().getUserId());
        Optional<User> newHostUserFromFile = userRepository.findUserById(channelHostUserUpdateRequestDTO.getNewHostUserResponseDto().getUserId());
        Optional<Channel> channelFromFile = channelRepository.findChannelByChannelId(channelHostUserUpdateRequestDTO.getChannelResponseDto().getChannelId());

        optionalUserIsEmpty(oldHostUserFromFile);
        optionalUserIsEmpty(newHostUserFromFile);
        optionalChannelIsEmpty(channelFromFile);

        User oldHostUser = oldHostUserFromFile.get();
        User newHostUser = newHostUserFromFile.get();
        Channel channel = channelFromFile.get();

        if (!(channel.getUserIds().contains(newHostUser.getId()) && channel.getUserIds().contains(oldHostUser.getId()))) {
            System.out.println("유저들이 해당 채널에 없어 변경할 수 없습니다.");
            return;
        }

        channel.setHostUserId(newHostUser.getId());
        channelRepository.updateChannel(channel);
    }


    @Override
    public List<ChannelResponseDto> findPublicChannel() {
        List<ChannelResponseDto> publicChannelsDTO = new ArrayList<>();
        List<Channel> channels = channelRepository.loadChannels();
        channels.stream()
                .filter(channel -> channel.getChannelType().equals(ChannelType.PUBLIC_CHANNEL))
                .forEach(channel -> {
                    publicChannelsDTO.add(new ChannelResponseDto(channel, messageRepository.findMessagesByChannelId(channel.getId()).get(messageRepository.findMessagesByChannelId(channel.getId()).size() -1 ).getId()));
                });


        return publicChannelsDTO;
    }

    @Override
    public List<ChannelResponseDto> findPrivateChannel(UserResponseDto userResponseDto) {
        List<ChannelResponseDto> publicChannelsDTO = new ArrayList<>();
        List<Channel> channels = channelRepository.findChannelsByUserId(userResponseDto.getUserId());
        channels.stream()
                .filter(channel -> channel.getChannelType().equals(ChannelType.PRIVATE_CHANNEL))
                .forEach(channel -> publicChannelsDTO.add(new ChannelResponseDto(channel, messageRepository.findMessagesByChannelId(channel.getId()).get(messageRepository.findMessagesByChannelId(channel.getId()).size() -1 ).getId())));

        return publicChannelsDTO;
    }

    @Override
    public ChannelResponseDto findChannelDTOByCannelId(UUID chanelId){
        Optional<Channel> channel = channelRepository.findChannelByChannelId(chanelId);
        optionalChannelIsEmpty(channel);

        List<Message> messages = messageRepository.findMessagesByChannelId(channel.get().getId());
        return new ChannelResponseDto(channel.get(), messages.get(messages.size()-1).getId());
    }

    @Override
    public List<ReadStatus> findAllReadStatus(){
        return readStatusRepository.loadReadStatuses();
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

    private void optionalChannelIsPresent(Optional<Channel> channel) {
        if (channel.isPresent()) {
            throw new IllegalArgumentException("Channel 정보가 중복됩니다.");
        }
    }
}
