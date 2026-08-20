package com.cms.dal.repository;

import com.cms.dal.entity.ResponseCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponseCodeRepository extends JpaRepository<ResponseCode, Long> {

    Optional<ResponseCode> findByCode(String code);
}
