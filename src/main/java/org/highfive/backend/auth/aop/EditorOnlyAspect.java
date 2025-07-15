package org.highfive.backend.auth.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.highfive.backend.curation.exception.CurationErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.UserRole;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EditorOnlyAspect {

    @Before("@annotation(org.highfive.backend.auth.aop.EditorOnly) && within(org.highfive.backend.curation.controller..*)")
    public void isValid() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!(principal instanceof User)) {
            throw new BusinessException(CurationErrorCode.CURATION_ACCESS_DENIED);
        }

        final User user = (User) principal;

        if(user.getUserRole().equals(UserRole.USER) || user.getUserRole().equals(UserRole.TEMP_USER)) {
            throw new BusinessException(CurationErrorCode.CURATION_ACCESS_DENIED);
        }
    }
}
