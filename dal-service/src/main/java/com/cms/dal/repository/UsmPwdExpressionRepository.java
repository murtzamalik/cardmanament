package com.cms.dal.repository;

import com.cms.dal.entity.UsmPwdExpression;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsmPwdExpressionRepository extends JpaRepository<UsmPwdExpression, Long> {

    Optional<UsmPwdExpression> findByPwdExpId(String pwdExpId);
}
