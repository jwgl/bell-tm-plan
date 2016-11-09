package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 专业设置
 * @author Yang Lin
 */
@PreAuthorize('hasAuthority("PERM_SUBJECT_SETUP")')
class SubjectSettingsController {
    def index() { }
}
