package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 教学计划模板
 * @author Yang Lin
 */
@PreAuthorize('hasAuthority("PERM_PROGRAM_SETUP")')
class SchemeTemplateController implements ServiceExceptionHandler {
    SchemeTemplateService schemeTemplateService
    def index() {
        renderJson schemeTemplateService.getList()
    }
}
