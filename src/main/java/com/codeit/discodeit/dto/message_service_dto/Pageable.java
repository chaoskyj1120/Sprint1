package com.codeit.discodeit.dto.message_service_dto;

import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pageable {

  @Min(0)
  int page;

  @Min(1)
  int size;

  private List<String> sort;
}
