package com.codeit.discodeit.dto.message_service_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
  private List<T> content;
  private int number;
  private int size;
  private boolean hasNext;
  private long totalElements;
}
