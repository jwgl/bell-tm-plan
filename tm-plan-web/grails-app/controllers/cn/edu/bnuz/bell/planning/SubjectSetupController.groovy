package cn.edu.bnuz.bell.planning

import org.springframework.security.access.annotation.Secured

/**
 * 专业设置
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_SUBJECT_SETUP'])
class SubjectSetupController {
    def index() { }
}
