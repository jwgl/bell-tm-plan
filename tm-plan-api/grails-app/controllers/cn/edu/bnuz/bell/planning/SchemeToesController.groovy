package cn.edu.bnuz.bell.planning

class SchemeToesController {
    SchemeDepartmentService schemeDepartmentService
    SchemeToesService schemeToesService

    def index(String departmentId, Long schemeDepartmentId) {
        def scheme = schemeDepartmentService.getScheme(departmentId, schemeDepartmentId)
        def programCourses = schemeToesService.getProgramCourses(scheme.programId)
        renderJson([scheme: scheme, programCourses: programCourses])
    }

    def save(String departmentId, Long schemeDepartmentId) {
        def cmd = new ProgramCoursesCommand()
        bindData(cmd, request.JSON)
        schemeToesService.save(departmentId, schemeDepartmentId, cmd)
        renderOk()
    }
}
