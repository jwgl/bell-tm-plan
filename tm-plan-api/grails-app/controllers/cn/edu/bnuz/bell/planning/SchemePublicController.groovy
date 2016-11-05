package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 教学计划公共
 * @author Yang Lin
 */
@PreAuthorize('hasAuthority("PERM_SCHEME_READ")')
class SchemePublicController implements ServiceExceptionHandler {
    SchemePublicService schemePublicService

    def index() {
        renderJson schemePublicService.getSchemes()
    }

    def show(Long id) {
        renderJson schemePublicService.getSchemeInfo(id)
    }

    def propertyCourses(Long schemePublicId, Integer propertyId) {
        renderJson schemePublicService.getPropertyCourses(schemePublicId, propertyId)
    }

    def directionCourses(Long schemePublicId, Integer directionId) {
        renderJson schemePublicService.getDirectionCourses(schemePublicId, directionId)
    }
}
