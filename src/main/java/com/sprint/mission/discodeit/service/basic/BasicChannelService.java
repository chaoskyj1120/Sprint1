package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel_service_dto.*;
import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
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
    public ChannelDTO createPublicChannel(CreateChannelRequestDTO createChannelRequestDTO) {

        User user = userRepository.findUserById(createChannelRequestDTO.getHostUserId());

        if (user == null) {
           throw new IllegalArgumentException("호스트 유저를 찾을 수 없습니다.");
        }

        Optional<Channel> duplicateChannel = channelRepository.findChannelByChannelName(createChannelRequestDTO.getChannelName());

        if (duplicateChannel.isPresent()) {
            System.out.println("중복 이름이 있는 채널 입니다.\n"); // 테스트를 위한 출력
            //throw new IllegalArgumentException("이미 같은 이름의 채널이 존재합니다."); 테스트를 위한 주석추리
            return new ChannelDTO(duplicateChannel.get());
        }

        Channel channel = new Channel(createChannelRequestDTO.getHostUserId(),
                createChannelRequestDTO.getChannelName(),
                createChannelRequestDTO.getDescription());

        User hostUser = userRepository.findUserById(createChannelRequestDTO.getHostUserId());
        channel.addUser(hostUser);
        hostUser.addChannel(channel);

        channelRepository.createChannel(channel);
        userRepository.updateUser(hostUser);

        //createOrUpdateReadStatus(hostUser, channel);

        return new ChannelDTO(channel);
    }

    @Override
    public ChannelDTO createPrivateChannel(CreateChannelRequestDTO createChannelRequestDTO, UserDTO enterUserDTO) {

        User hostUser = userRepository.findUserById(createChannelRequestDTO.getHostUserId());

        if (hostUser == null) {
            throw new IllegalArgumentException("호스트 유저를 찾을 수 없습니다.");
        }

        User enterUser = userRepository.findUserById(enterUserDTO.getUserId());

        if (enterUser == null) {
            throw new IllegalArgumentException("입장할 유저를 찾을 수 없습니다.");
        }

        List<Channel> channelsInHostUser = channelRepository.findChannelsByUserId(hostUser.getId());
        Optional<Channel> duplicateChannel = channelsInHostUser.stream().filter(ch -> ch.getUserIds().contains(enterUserDTO.getUserId())).findFirst();
        if (duplicateChannel.isPresent()) {
            System.out.println("중복 된 채널 입니다.\n"); // 테스트를 위한 출력
            //throw new IllegalArgumentException("중복된 채널입니다.");// 테스트를 위한 주석추리
            return new ChannelDTO(duplicateChannel.get());
        }

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

        return new ChannelDTO(channel);
    }

    @Override
    public void updateChannelName(ChannelNameUpdateRequestDTO channelNameUpdateRequestDTO) {
        // 1. 채널의 호스티인지 검증
        // 2. 변경된 채넝을 넘겨주기
        User user = userRepository.findUserById(channelNameUpdateRequestDTO.getUserDTO().getUserId());
        Channel channel = channelRepository.getChannelById(channelNameUpdateRequestDTO.getChannelDTO().getChannelId());

        if (!user.equalsId(channel.getHostUserId())){
            System.out.println("해당 채널의 호스트가 아니라 권한이 없습니다.\n"); //테스트용 나중에 throw로 바꾸기
            return;
        }

        Optional<Channel> duplicateNewName = channelRepository.findChannelByChannelName(channelNameUpdateRequestDTO.getChannelNewName());
        if (duplicateNewName.isPresent()){
            System.out.println("새로운 이름과 동일한 이름을 가진 채널 이름이 이미 존재합니다. \n");
            return;
        }

        channel.setChannelName(channelNameUpdateRequestDTO.getChannelNewName());
        channelRepository.updateChannel(channel);
    }

    @Override
    public void deleteChannel(DeleteChannelRequestDTO deleteChannelRequestDTO) {
        User user = userRepository.findUserById(deleteChannelRequestDTO.getUserDTO().getUserId());
        Channel channel = channelRepository.getChannelById(deleteChannelRequestDTO.getChannelDTO().getChannelId());

        if (user.getStatus() == UserActivationState.DEACTIVE || channel == null) return;

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


        readStatusRepository.deleteReadStatusByChannelId(deleteChannelRequestDTO.getChannelDTO().getChannelId());
        messageRepository.deleteMessagesByChannelId(channel.getId());
        userRepository.saveUsers(users);
        channelRepository.deleteChannel(channel);
    }

    @Override
    public void addUserToChannel(AddUserToChannelRequestDTO addUserToChannelRequestDTO) {
        User user = userRepository.findUserById(addUserToChannelRequestDTO.getUserDTO().getUserId());
        Channel channel = channelRepository.getChannelById(addUserToChannelRequestDTO.getChannelDTO().getChannelId());

        if (user.getStatus() == UserActivationState.DEACTIVE){
            System.out.println("유저 상태가 비활성입니다.");
            return;
        }

        if (channel == null) {
            System.out.println("채널이 Null 입니다.");
            return;
        }

        User userFromFile = userRepository.findUserById(user.getId());
        userFromFile.addChannel(channel);
        userRepository.updateUser(userFromFile);

        Channel channelFromFile = channelRepository.getChannelById(channel.getId());
        channelFromFile.addUser(user);
        channelRepository.updateChannel(channelFromFile);
    }

    @Override
    public void leaveUserFromChannel(LeaveUserFromChannelRequestDTO leaveUserFromChannelRequestDTO) {

        User userFromFile = userRepository.findUserById(leaveUserFromChannelRequestDTO.getUserDTO().getUserId());
        Channel channelFromFile = channelRepository.getChannelById(leaveUserFromChannelRequestDTO.getChannelDTO().getChannelId());

        if (userFromFile == null || channelFromFile == null) {
            System.out.println("유저 또는 채널을 찾을 수 없습니다.");
            return;
        }

        userFromFile.removeChannel(channelFromFile);
        userRepository.updateUser(userFromFile);
        // 유저 찾기 -> 해당 채널 삭제 -> 유저 업데이트

        channelFromFile.removeUser(userFromFile);
        channelRepository.updateChannel(channelFromFile);
    }

    @Override
    public void updateHostUser(ChannelHostUserUpdateRequestDTO channelHostUserUpdateRequestDTO) {

        User oldHostUser = userRepository.findUserById(channelHostUserUpdateRequestDTO.getOldHostUserDTO().getUserId());
        User newHostUser = userRepository.findUserById(channelHostUserUpdateRequestDTO.getNewHostUserDTO().getUserId());
        Channel channel = channelRepository.getChannelById(channelHostUserUpdateRequestDTO.getChannelDTO().getChannelId());

        // 1. 진짜 호스트인가?
        // 2. 모두 존재하는 가?

        if (oldHostUser == null || newHostUser == null || channel == null) {
            System.out.println("유저 또는 채널을 찾을 수 없습니다.");
            return;
        }

        if (!channel.getHostUserId().equals(oldHostUser.getId())) {
            System.out.println("채널 호스트가 아니라 변경할 수 없습니다.");
            return;
        }

        if (!(channel.getUserIds().contains(newHostUser.getId()) && channel.getUserIds().contains(oldHostUser.getId()))) {
            System.out.println("유저들이 해당 채널에 없어 변경할 수 없습니다.");
            return;
        }

        channel.setHostUserId(newHostUser.getId());
        channelRepository.updateChannel(channel);
    }

    @Override
    public ChannelDTO enterChanner(User user, Channel chanel){
        // 채널에 가입된 유저가 채널에 입장했을 떄 / 가입이랑 다름
        // 채널을 반환해야 하나?
        // TODO ReadStatus를 추가 또는 변경
        // 미구현
        return null;
    }


    @Override
    public List<ChannelDTO> findPublicChannel() {
        List<ChannelDTO> publicChannelsDTO = new ArrayList<>();
        List<Channel> channels = channelRepository.loadChannels();
        channels.stream()
                .filter(channel -> channel.getChannelType().equals(ChannelType.PUBLIC_CHANNEL))
                .forEach(channel -> {
                    publicChannelsDTO.add(new ChannelDTO(channel));
                });

        return publicChannelsDTO;
    }

    @Override
    public List<ChannelDTO> findPrivateChannel(UserDTO userDTO) {
        List<ChannelDTO> publicChannelsDTO = new ArrayList<>();
        List<Channel> channels = channelRepository.findChannelsByUserId(userDTO.getUserId());
        channels.stream()
                .filter(channel -> channel.getChannelType().equals(ChannelType.PRIVATE_CHANNEL))
                .forEach(channel -> publicChannelsDTO.add(new ChannelDTO(channel)));

        return publicChannelsDTO;
    }

    @Override
    public ChannelDTO findChannelDTOByCannelId(UUID chanelId){
        Channel channel = channelRepository.getChannelById(chanelId);
        return new ChannelDTO(channel);
    }

    @Override
    public List<ReadStatus> findAllReadStatus(){
        return readStatusRepository.loadReadStatuses();
    }




}
