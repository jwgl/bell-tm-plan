package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_SCHEME_ADMIN")')
class DepartmentController {
    DepartmentService departmentService

    def index() {
        renderJson departmentService.getDepartments()
    }

    def grades(String departmentId) {
        renderJson departmentService.getGrades(departmentId)
    }
}
