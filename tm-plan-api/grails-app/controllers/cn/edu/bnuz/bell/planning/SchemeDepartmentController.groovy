package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 学院管理教学计划
 * EndPoint /departments/01/schemes
 */
@PreAuthorize('hasAuthority("PERM_SCHEME_DEPT_ADMIN")')
class SchemeDepartmentController {
    SchemeDepartmentService schemeDepartmentService

    def index(String departmentId) {
        renderJson schemeDepartmentService.getSchemes(departmentId)
    }

    def show(String departmentId, Long id) {
        renderJson schemeDepartmentService.getScheme(departmentId, id)
    }

    @PreAuthorize('hasAuthority("PERM_SCHEME_WRITE")')
    def latest(String departmentId) {
        renderJson schemeDepartmentService.getLatestSchemes(departmentId)
    }

    @PreAuthorize('hasAuthority("PERM_SCHEME_WRITE")')
    def directions(String departmentId) {
        renderJson schemeDepartmentService.getLatestDirections(departmentId)
    }
}
