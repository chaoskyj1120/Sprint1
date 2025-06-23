package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {
	public static final String pathStaticFolder = "./src/main/resources/static";
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		mainTest(context);
	}

	public static void mainTest(ConfigurableApplicationContext context) {
		// 1. 유저 생성
		// 2. 로그인
		// 3. 전체 유저의 로그인/로그아웃 상태 확인
 		// 4.
 		//
		deleteAllFilesInDataFolder();

		UserService userService = context.getBean(UserService.class);
		AuthService authService = context.getBean(AuthService.class);

		System.out.println("\n========== userService Test start ==========================================\n");
		System.out.println("1. 유저 등록=================");

		String profilePicture1Path =  pathStaticFolder + "/basicUserProfileImage.png"; // 기본 이미지, 유저가 선택하면 여기 값이 바뀌도록 해서 기본 이미지가 있도록 유지
		String profilePicture2Path = null; // 나중에서 이미지 선택 로직 추가, 이미지의 경로를 추가하면 좋을 듯

		UserCreateDTO userCreateDTO1 = new UserCreateDTO("id1", "pw1", "권용진-1", "kwon1@email.com", profilePicture1Path);
		UserCreateDTO userCreateDTO2 = new UserCreateDTO("id2", "pw2", "권용진-2", "kwon2@email.com", profilePicture1Path);

		User user1 = userService.createUser(userCreateDTO1);
		User user2 = userService.createUser(userCreateDTO2);

		AuthService basicAuthService = context.getBean(BasicAuthService.class);
		LoginRequestDTO loginRequestDTO = new LoginRequestDTO("id1", "pw1");
		UserLoginDataDTO loginUser1 = basicAuthService.logInUser(loginRequestDTO);
		System.out.println(loginUser1.getUserName() + "님이 로그인에 성공했습니다.");

		/*
		System.out.println(user1.getProfileId());
		BinaryContentsRepository binaryContentsRepository = context.getBean(BinaryContentsRepository.class);
		BinaryContents user1ProfileImg = binaryContentsRepository.getBinaryContentsById(user1.getProfileId());
		testImgBySave(user1ProfileImg.getBinaryData()); 이미지 제대로 저장되었는지 확인
		*/

		List<UserLoggedDataDTO> userLoggedDataDTOS = userService.findAll();
		System.out.println("=== 전체 유저 로그인 상태 ===");
		userLoggedDataDTOS.forEach(userLoginData -> System.out.println("사용자: " + userLoginData.getUserName() + " | 상태: " + userLoginData.getIsLoggedIn()));
		System.out.println();

		System.out.println("=== 단일 유저 로그인 상태 ===");
		UserLoggedDataDTO userLoggedDataDTO = userService.findUserById("id1");
		System.out.println("사용자: " + userLoggedDataDTO.getUserName() + " | 상태: " + userLoggedDataDTO.getIsLoggedIn());
		System.out.println();

		System.out.println("=== 유저 업데이트 ===");
		UserUpdateDTO userUpdateDto = new UserUpdateDTO(user1.getId(), "newId1", "new권용진-1", "newPw1", "newEMail", ".\\src\\main\\resources\\static\\basicUserProfileImage2.png");
		userService.updateUser(userUpdateDto);
		userLoggedDataDTO = userService.findUserById("newId1");
		System.out.println("사용자: " + userLoggedDataDTO.getUserName() + " | 상태: " + userLoggedDataDTO.getIsLoggedIn());
		System.out.println();

		System.out.println("=== 전체 유저 로그인 상태 ===");
		userLoggedDataDTOS = userService.findAll();
		userLoggedDataDTOS.forEach(userLoginData -> System.out.println("사용자: " + userLoginData.getUserName() + " | 상태: " + userLoginData.getIsLoggedIn()));
		System.out.println();

		System.out.println("=== 유저 삭제 전 전체 유저 로그인 상태 ===");
		userLoggedDataDTOS = userService.findAll();
		userLoggedDataDTOS.forEach(userLoginData -> System.out.println("사용자: " + userLoginData.getUserName() + " | 상태: " + userLoginData.getIsLoggedIn()));
		System.out.println();
		// 꺼내서 확인하기

		System.out.println("=== 유저 삭제 전 UserStatus 확인 ===");
		UserStatusRepository userStatusRepository = context.getBean(UserStatusRepository.class);
		userStatusRepository.loadUserStatuses().forEach(status -> System.out.println(status.getUserId()));
		userService.printAllUsers();
		System.out.println();

		System.out.println("=== 유저 삭제 전 BinaryContents 확인 ===");
		BinaryContentsRepository binaryContentsRepository = context.getBean(BinaryContentsRepository.class);
		binaryContentsRepository.loadBinaryContents().forEach(binaryContent -> System.out.println("프로필 ID: " + binaryContent.getId() + ", User Id: "+ binaryContent.getReferenceId()));
		System.out.println();
		
		
		System.out.println("=== 유저 삭제 후 전체 유저 로그인 상태 ===");
		userService.deleteUser(user1);
		userLoggedDataDTOS = userService.findAll();
		userLoggedDataDTOS.forEach(userLoginData -> System.out.println("사용자: " + userLoginData.getUserName() + " | 상태: " + userLoginData.getIsLoggedIn()));
		System.out.println();
		// 꺼내서 확인하기

		System.out.println("=== 유저 삭제 후 UserStatus 확인 ===");
		userStatusRepository = context.getBean(UserStatusRepository.class);
		userStatusRepository.loadUserStatuses().forEach(status -> System.out.println(status.getUserId()));
		userService.printAllUsers();
		System.out.println();

		System.out.println("=== 유저 삭제 후 BinaryContents 확인 ===");
		binaryContentsRepository = context.getBean(BinaryContentsRepository.class);
		binaryContentsRepository.loadBinaryContents().forEach(binaryContent -> System.out.println("프로필 ID: " + binaryContent.getId() + ", User Id: "+ binaryContent.getReferenceId()));
		System.out.println();


	}

	public static void testImgBySave(byte[] imageBytes){

		File outputFile = new File(pathStaticFolder + "/restored_user1_profile.jpg"); // 저장될 파일명
		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			fos.write(imageBytes);
			System.out.println("이미지 파일로 저장 완료: " + outputFile.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("이미지 저장 실패: " + e.getMessage());
		}
	}

	public static void deleteAllFilesInDataFolder() {
		File folder = new File("./data");

		if (!folder.exists()) {
			System.out.println("data 폴더가 존재하지 않습니다.");
			return;
		}

		File[] files = folder.listFiles();

		if (files == null || files.length == 0) {
			System.out.println("data 폴더에 삭제할 파일이 없습니다.");
			return;
		}

		for (File file : files) {
			if (file.isFile()) {
				boolean deleted = file.delete();
				System.out.printf("파일 %s 삭제 %s%n", file.getName(), deleted ? "성공" : "실패");
			}
		}
	}

}