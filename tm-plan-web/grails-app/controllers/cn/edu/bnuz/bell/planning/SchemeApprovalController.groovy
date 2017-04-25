package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_SCHEME_APPROVE")')
class SchemeApprovalController {
    def index() { }
}
