package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.TermService

class SchemeToesController {
    SchemeDepartmentService schemeDepartmentService
    SchemeToesService schemeToesService
    TermService termService

    def index(String departmentId, Long schemeDepartmentId) {
        def scheme = schemeDepartmentService.getScheme(departmentId, schemeDepartmentId)
        def programCourses = schemeToesService.getProgramCourses(scheme.programId)
        def termId = termService.activeTerm.id
        def schemeTerm = (termId.intdiv(10) - scheme.grade) * 2 + termId % 10

        renderJson([
                scheme        : scheme,
                programCourses: programCourses,
                schemeTerm    : schemeTerm,
        ])
    }

    def save(String departmentId, Long schemeDepartmentId) {
        def cmd = new ProgramCoursesCommand()
        bindData(cmd, request.JSON)
        schemeToesService.save(departmentId, schemeDepartmentId, cmd)
        renderOk()
    }
}
