package cn.edu.bnuz.bell.planning

class VisionDepartmentController {
    VisionDepartmentService visionDepartmentService

    def index(String departmentId) {
        renderJson visionDepartmentService.getVisionsByDepartment(departmentId)
    }
}
