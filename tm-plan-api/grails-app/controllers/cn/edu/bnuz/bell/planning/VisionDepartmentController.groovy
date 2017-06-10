package cn.edu.bnuz.bell.planning

import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_VISION_DEPT_ADMIN")')
class VisionDepartmentController {
    VisionDepartmentService visionDepartmentService

    def index(String departmentId) {
        renderOk()
    }

    @PreAuthorize('hasAuthority("PERM_VISION_WRITE")')
    def latest(String departmentId) {
        renderJson visionDepartmentService.getLatestVisions(departmentId)
    }
}
