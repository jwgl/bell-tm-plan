package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_VISION_CHECK")')
class VisionCheckController {
    def index() { }
}
