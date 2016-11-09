package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_SUBJECT_SETUP")')
class SubjectSettingsController implements ServiceExceptionHandler {
    SubjectSettingsService subjectSettingsService
    def index() {
        renderJson subjectSettingsService.getList()
    }

    def update(String id) {
        SubjectSettingsCommand cmd = new SubjectSettingsCommand()
        cmd.subjectId = id
        bindData cmd, request.JSON
        subjectSettingsService.update(cmd)
        renderOk()
    }
}
