package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 审核培养方案。
 * @author Yang Lin
 */
@PreAuthorize('hasAnyAuthority("PERM_SCHEME_CHECK", "PERM_SCHEME_APPROVE")')
class SchemeReviewController {
    def show() {}
}
