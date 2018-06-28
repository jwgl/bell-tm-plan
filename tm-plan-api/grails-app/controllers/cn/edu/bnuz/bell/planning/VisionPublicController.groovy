package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 培养方案公共控制器。
 * @author Yang Lin
 */
@PreAuthorize('hasAuthority("PERM_VISION_READ")')
class VisionPublicController implements ServiceExceptionHandler {
    /**
     * 所有在校年级培养方案
     */
    VisionPublicService visionPublicService
    def index() {
        renderJson visionPublicService.getAllVisions()
    }

    /**
     * 获取指定ID培养方案
     * @param id 培养方案ID
     */
    def show(Long id) {
        renderJson visionPublicService.getVisionInfo(id)
    }
}
