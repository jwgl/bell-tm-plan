package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 教学计划设置
 * @author Yang Lin
 */
@PreAuthorize('hasAuthority("PERM_PROGRAM_SETUP")')
class ProgramSettingsController implements ServiceExceptionHandler{
    ProgramSettingsService programSettingsService

    def index() {
        def grade = params.int('grade')
        if (grade) {
            renderJson programSettingsService.getListByGrade(grade)
        } else {
            renderJson programSettingsService.getList()
        }
    }

    def update(Integer id) {
        ProgramSettingsCommand cmd = new ProgramSettingsCommand()
        bindData cmd, request.JSON
        programSettingsService.update(id, cmd)
        renderOk()
    }

    def grades() {
        renderJson programSettingsService.getGrades()
    }
}
