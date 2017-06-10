package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_SCHEME_ADMIN")')
class SchemeAdminController {
    SchemeAdminService schemeAdminService

    def index() {
        String departmentId = params.departmentId
        Integer grade = params.int('grade')
        renderJson schemeAdminService.getSchemes(departmentId, grade)
    }

    def show(Long id) {
        renderJson schemeAdminService.getScheme(id)
    }
}
