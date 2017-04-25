package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_SCHEME_CHECK")')
class SchemeCheckController implements ServiceExceptionHandler {
    SchemeCheckService schemeCheckService
    SchemeReviewerService schemeReviewerService

    def index(String checkerId, ListCommand cmd) {
        renderJson schemeCheckService.list(checkerId, cmd)
    }

    def show(String checkerId, Long schemeCheckId, String id, String type) {
        ListType listType= ListType.valueOf(type)
        if (id == 'undefined') {
            renderJson schemeCheckService.getSchemeForReview(checkerId, schemeCheckId, listType, Activities.CHECK)
        } else {
            renderJson schemeCheckService.getSchemeForReview(checkerId, schemeCheckId, listType, UUID.fromString(id))
        }
    }

    def patch(String checkerId, Long schemeCheckId, String id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = schemeCheckId
                schemeCheckService.accept(checkerId, cmd, UUID.fromString(id))
                break
            case Event.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = schemeCheckId
                schemeCheckService.reject(checkerId, cmd, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        show(checkerId, schemeCheckId, id, 'todo')
    }

    def approvers(String checkerId, Long schemeCheckId) {
        renderJson schemeReviewerService.getApprovers()
    }
}
