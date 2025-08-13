package com.codeit.discodeit.fakerepository;

import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeUserRepository implements UserRepository {
  private final Map<UUID, User> userStore = new HashMap<>();

  @Override
  public User createUser(User user){
    userStore.put(user.getId(), user);
    return user;
  }

  @Override
  public User updateUser(User user){
    userStore.put(user.getId(), user); // 기존 유저의 정보가 덮어씌워짐
    return user;
  }

  @Override
  public void deleteUser(User user){
    userStore.remove(user.getId());
  }

  @Override
  public List<User> loadUsers(){
    return new ArrayList<>(userStore.values());
  }

  @Override
  public Optional<User> findUserByUserId(UUID id){
    return Optional.ofNullable(userStore.get(id));
  }

  @Override
  public Optional<User> findUserByEmail(String email){
    User user;
    for (UUID id : userStore.keySet()) {
      user = userStore.get(id);
      if (user.getEmail().equals(email)) {
        return Optional.of(user);
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> findUserByUserName(String username){
    User user;
    for (UUID id : userStore.keySet()) {
      user = userStore.get(id);
      if (user.getUsername().equals(username)) {
        return Optional.of(user);
      }
    }
    return Optional.empty();
  }

  @Override
  public List<User> findUserListByUserIdList(List<UUID> userIdList){
    List<User> users = new ArrayList<>();
    for (UUID id : userIdList) {
      users.add(userStore.get(id));
    }
    return users;
  }
}
