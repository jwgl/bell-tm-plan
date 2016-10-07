package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 教学计划设置
 * @author Yang Lin
 */
@PreAuthorize('hasAuthority("PERM_PROGRAM_SETUP")')
class ProgramSetupController {
    def index() { }
}
