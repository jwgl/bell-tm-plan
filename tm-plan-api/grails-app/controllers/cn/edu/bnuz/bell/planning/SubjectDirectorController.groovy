package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_SUBJECT_SETUP")')
class SubjectDirectorController implements ServiceExceptionHandler {
    SubjectDirectorService subjectDirectorService
    def index() {
        renderJson subjectDirectorService.getSubjectDirectors()
    }

    def save() {
        SubjectDirectorCommand cmd = new SubjectDirectorCommand()
        bindData cmd, request.JSON
        subjectDirectorService.save(cmd)
        renderOk()
    }
}
