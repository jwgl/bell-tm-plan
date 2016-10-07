package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 编辑培养方案。
 * @author Yang Lin
 */
@PreAuthorize('hasAuthority("PERM_VISION_WRITE")')
class VisionDraftController {
    def index() {}
}
