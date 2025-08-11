package com.codeit.discodeit.controller;

import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.mapper.BinaryContentMapper;
import com.codeit.discodeit.dto.user_service_dto.UserCreateRequest;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.dto.user_service_dto.UserUpdateRequest;
import com.codeit.discodeit.dto.user_status_dto.UserStatusDto;
import com.codeit.discodeit.dto.user_status_dto.UserStatusUpdateRequest;
import com.codeit.discodeit.service.UserService;
import com.codeit.discodeit.service.UserStatusService;
import com.codeit.discodeit.swagger.SwaggerUserController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
@RequestMapping("/api/users")
public class UserController implements SwaggerUserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAllUser();
    return ResponseEntity.ok(users);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> createUser(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {

    userCreateRequest.setProfileImage(profile);
    UserDto createdUser = userService.createUser(userCreateRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
    userService.findUserDtoByUserId(userId); // 존재하지 않으면 내부에서 예외 발생
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build(); // 204 No Content
  }

  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> updateUser(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") @Valid UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profileImage)
      throws IOException {

    userUpdateRequest.setUserId(userId);

    BinaryContent profileImg = BinaryContentMapper.attachmentToBinaryContent(profileImage);
    UserDto updatedUser = userService.updateUser(userUpdateRequest, profileImg, profileImage.getBytes());
    return ResponseEntity.ok(updatedUser);
  }

  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request
  ) {
    UserStatusDto updatedStatus = userStatusService.updateUserStatus(userId,
        request.getNewLastActiveAt());

    return ResponseEntity.ok(updatedStatus);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
    UserDto user = userService.findUserDtoByUserId(userId);
    return ResponseEntity.ok(user);
  }
}
