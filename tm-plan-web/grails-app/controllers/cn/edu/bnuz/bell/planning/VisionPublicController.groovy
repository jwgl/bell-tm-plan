package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 培养方案。
 * @author Yang Lin
 */
@PreAuthorize('hasAuthority("PERM_VISION_READ")')
class VisionPublicController {
    def index() {}

    def show() {}
}