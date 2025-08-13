package com.codeit.discodeit.fakerepository;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.repository.BinaryContentRepository;

import java.util.*;

public class FakeBinaryContentRepository implements BinaryContentRepository {

  private final Map<UUID, BinaryContent> store = new HashMap<>();

  @Override
  public void createBinaryContent(BinaryContent contents) {
    if (contents == null || contents.getId() == null) {
      throw new IllegalArgumentException("BinaryContent 또는 ID가 null일 수 없습니다.");
    }
    store.put(contents.getId(), contents);
  }

  @Override
  public void deleteBinaryContent(BinaryContent binaryContent) {
    if (binaryContent != null) {
      store.remove(binaryContent.getId());
    }
  }

  @Override
  public Optional<BinaryContent> findBinaryContentByBinaryContentId(UUID binaryContentsId) {
    return Optional.ofNullable(store.get(binaryContentsId));
  }

  @Override
  public List<BinaryContent> findBinaryContentListByBinaryContentIds(List<UUID> binaryContentIds) {
    List<BinaryContent> result = new ArrayList<>();
    for (UUID id : binaryContentIds) {
      BinaryContent content = store.get(id);
      if (content != null) {
        result.add(content);
      }
    }
    return result;
  }

  // 테스트 확인용: 전체 저장소 반환
  public List<BinaryContent> loadAll() {
    return new ArrayList<>(store.values());
  }
}
