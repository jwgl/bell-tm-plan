package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 审核培养方案。
 * @author Yang Lin
 */
@PreAuthorize('hasAnyAuthority("PERM_VISION_CHECK", "PERM_VISION_APPROVE")')
class VisionReviewController {
    def show() {}
}
