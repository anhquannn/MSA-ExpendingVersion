package com.market.MSA.services.others;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class EntityFinderService {
  public <T, ID> T findByIdOrThrow(JpaRepository<T, ID> repository, ID id, ErrorCode errorCode) {
    return repository.findById(id).orElseThrow(() -> new AppException(errorCode));
  }
}
