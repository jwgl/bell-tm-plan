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

    /**
     * 按学院获取教学计划列表
     * EndPoint /departments/01/schemes
     * @param id 学院ID
     */
    def indexByDepartment(String departmentId) {
        renderJson schemePublicService.getSchemesByDepartment(departmentId)
    }

    def show(Long id) {
        renderJson schemePublicService.getSchemeInfo(id)
    }

    def directionsByDepartment(String departmentId) {
        renderJson schemePublicService.getDirectionsByDepartment(departmentId)
    }

    def propertyCourses(Long schemePublicId, Integer propertyId) {
        renderJson schemePublicService.getPropertyCourses(schemePublicId, propertyId)
    }

    def directionCourses(Long schemePublicId, Integer directionId) {
        renderJson schemePublicService.getDirectionCourses(schemePublicId, directionId)
    }
}
