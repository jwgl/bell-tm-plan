package cn.edu.bnuz.bell.planning

class SchemeDepartmentController {
    SchemeDepartmentService schemeDepartmentService

    /**
     * 按学院获取教学计划列表
     * EndPoint /departments/01/schemes
     * @param id 学院ID
     */
    def index(String departmentId) {
        renderJson schemeDepartmentService.getSchemesByDepartment(departmentId)
    }

    def directions(String departmentId) {
        renderJson schemeDepartmentService.getDirectionsByDepartment(departmentId)
    }
}
