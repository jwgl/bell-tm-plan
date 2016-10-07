package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 教学计划。
 * @author Yang Lin
 */
@PreAuthorize('hasAuthority("PERM_SCHEME_READ")')
class SchemePublicController {
    def index() {}

    def show() {}
}
