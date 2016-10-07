package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 编辑教学计划
 * @author Yang Lin
 */
@PreAuthorize('hasAuthority("PERM_SCHEME_WRITE")')
class SchemeDraftController {
    def index() {}
}
