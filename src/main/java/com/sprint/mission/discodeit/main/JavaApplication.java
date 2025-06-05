package com.sprint.mission.discodeit.main;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {

    public static void main(String[] args) {
        System.out.println("start!!\n");

        System.out.println("\n========== userService Test start ==========================================\n");
        userTestApplication();
        System.out.println("\n========== userService Test end ==========================================\n");
        // userService 테스트
        System.out.println("\n========== channelService Test start ==========================================\n");
        channelTestApplication();
        System.out.println("\n========== channelService Test end ==========================================\n");
        // channelService 테스트
        System.out.println("\n========== messageService Test start ==========================================\n");
        messageTestApplication();
        System.out.println("\n========== messageService Test end ==========================================\n");
        // messageService 테스트
    }

    public static void userTestApplication() {
        UserService jcfUserService = JCFUserService.getInstance();
        /*
        [ ] 등록
        [ ] 조회(단건, 다건)
        [ ] 수정
        [ ] 수정된 데이터 조회
        [ ] 삭제
        [ ] 조회를 통해 삭제되었는지 확인
        */

        User user1 = new User("권용진-1");
        User user2 = new User("권용진-2");

        jcfUserService.addUser(user1); // 유저 등록
        System.out.println();
        jcfUserService.addUser(user2);
        System.out.println();

        jcfUserService.printUser(user1);
        System.out.println();

        jcfUserService.printAllUsers();
        System.out.println();

        jcfUserService.updateUser(user1, "수정된 권용진-1");
        System.out.println();

        jcfUserService.printUser(user1);
        System.out.println();

        jcfUserService.deleteUser(user1); //삭제
        System.out.println();

        jcfUserService.printAllUsers(); // 조회
        System.out.println();
    }

    public static void channelTestApplication() {
        UserService jcfUserService = JCFUserService.getInstance();
        ChannelService jcfChannelService = JCFChannelService.getInstance();
        /*
        [ ] 등록
        [ ] 조회(단건, 다건)
        [ ] 수정
        [ ] 수정된 데이터 조회
        [ ] 삭제
        [ ] 조회를 통해 삭제되었는지 확인
        */
        // 권용진 2가 메소드에 추가 되어 있음
        User user1 = new User("권용진-1");
        User user2 = new User("권용진-2");

        jcfUserService.addUser(user1); // 유저 등록
        jcfUserService.addUser(user2);
        System.out.println();

        Channel channel1 = jcfChannelService.createChannel(user1, "권용진-1-서버");
        Channel channel2 = jcfChannelService.createChannel(user2, "권용진-2-서버");
        System.out.println();

        jcfChannelService.printAllChannels();
        System.out.println();
        jcfChannelService.printChannel(channel1);
        System.out.println();

        // 수정및 조회
        jcfChannelService.userJoinChannel(user2, channel1); // user2 번이 1번 채널에 추가됨
        jcfChannelService.printUsersFromChannel(channel1);
        System.out.println();

        jcfChannelService.updateChannelName(user1, channel1, "권용진-1-서버의 새이름");// 채널 이름 변경
        System.out.println();

        jcfChannelService.printAllChannels();
        System.out.println();

        // 채널 떠남
        jcfChannelService.userLeaveChannel(user2, channel1);
        jcfChannelService.printChannel(channel1);
        System.out.println();

        //주인 변경
        jcfChannelService.updateHostUser(user2, channel1, user1);
        jcfChannelService.updateHostUser(user1, channel1, user2);
        System.out.println();

        // 채널 삭제 및 조회
        jcfChannelService.deleteChannel(user1 ,channel1); // "권용진-1-서버" 채널 삭제
        System.out.println();

        jcfChannelService.printAllChannels();
        System.out.println();
    }

    public static void messageTestApplication() {

        UserService jcfUserService = JCFUserService.getInstance();
        ChannelService jcfChannelService = JCFChannelService.getInstance();
        MessageService jcfMessageService = JCFMessageService.getInstance();
        /*
        [ ] 등록
        [ ] 조회(단건, 다건)
        [ ] 수정, 의존성
        [ ] 수정된 데이터 조회
        [ ] 삭제
        [ ] 조회를 통해 삭제되었는지 확인
        */

        User user1 = new User("권용진-1");
        User user2 = new User("권용진-2");

        jcfUserService.addUser(user1); // 유저 등록
        System.out.println(); // 가독성을 위한 개행

        Channel channel1 = jcfChannelService.createChannel(user1, "권용진-1의 채널1");
        Channel channel2 = jcfChannelService.createChannel(user1, "권용진-1의 채널2");

        System.out.println(); // 가독성을 위한 개행

        Message message1 = jcfMessageService.createMessage(user1, channel1, "채널1의 테스트 메세지1 입니다"); // 메세지 등록
        Message message2 = jcfMessageService.createMessage(user1, channel1, "채널1의 테스트 메세지2 입니다"); // 메세지 등록

        Message message3 = jcfMessageService.createMessage(user1, channel2, "채널2의 테스트 메세지1 입니다"); // 메세지 등록
        System.out.println();

        jcfMessageService.printAllMessageByUser(user1); //유저 별 조회
        System.out.println();

        jcfMessageService.printMessagesByChannel(channel1); // 채널 별 조회
        System.out.println();

        jcfMessageService.printMessagesByChannel(channel2); // 채널 별 조회
        System.out.println();

        jcfMessageService.printMessage(message1);
        System.out.println();

        jcfMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        // 메세지 전체 조회, 채널별 조회, 메세지 아이디 별 조회
        // 수정 해야 됨
        jcfMessageService.updateMessage(user1, message1, "채널 1의 테스트 메세지의 새로운 업데이트입니다.");
        System.out.println();

        jcfMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        jcfMessageService.deleteMessage(user2, message1);
        jcfMessageService.deleteMessage(user1, message1);
        System.out.println();

        jcfMessageService.printAllMessage(); // 전체 조회
        System.out.println();
    }
}
