package org.highfive.backend.auth.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.highfive.backend.content.exception.ContentErrorCode;
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
public class AdminOnlyAspect {

    @Before("@annotation(org.highfive.backend.auth.aop.AdminOnly) && within(org.highfive.backend.content.controller..*)")
    public void isValid() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!(principal instanceof User)) {
            throw new BusinessException(ContentErrorCode.CONTENT_ACCESS_DENIED);
        }

        final UserRole userRole = ((User) principal).getUserRole();

        if(userRole.equals(UserRole.USER) || userRole.equals(UserRole.TEMP_USER) || userRole.equals(UserRole.EDITOR)) {
            throw new BusinessException(ContentErrorCode.CONTENT_ACCESS_DENIED);
        }
    }
}
