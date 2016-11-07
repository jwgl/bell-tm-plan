package cn.edu.bnuz.bell.planning

/**
 * 学院管理教学计划
 * EndPoint /departments/01/schemes
 */
class SchemeDepartmentController {
    SchemeDepartmentService schemeDepartmentService

    def index(String departmentId) {
        renderJson schemeDepartmentService.getSchemes(departmentId)
    }

    def show(String departmentId, Long id) {
        renderJson schemeDepartmentService.getScheme(departmentId, id)
    }

    def latest(String departmentId) {
        renderJson schemeDepartmentService.getLatestSchemes(departmentId)
    }

    def directions(String departmentId) {
        renderJson schemeDepartmentService.getLatestDirections(departmentId)
    }
}
