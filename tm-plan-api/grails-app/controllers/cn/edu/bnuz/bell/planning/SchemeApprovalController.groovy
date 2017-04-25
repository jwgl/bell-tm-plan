package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_SCHEME_APPROVE")')
class SchemeApprovalController {
    SchemeApprovalService schemeApprovalService

    def index(String approverId, ListCommand cmd) {
        renderJson schemeApprovalService.list(approverId, cmd)
    }

    def show(String approverId, Long schemeApprovalId, String id, String type) {
        ListType listType = Enum.valueOf(ListType, type)
        if (id == 'undefined') {
            renderJson schemeApprovalService.getSchemeForReview(approverId, schemeApprovalId, listType, Activities.APPROVE)
        } else {
            renderJson schemeApprovalService.getSchemeForReview(approverId, schemeApprovalId, listType, UUID.fromString(id))
        }
    }

    def patch(String approverId, Long schemeApprovalId, String id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = schemeApprovalId
                schemeApprovalService.accept(approverId, cmd, UUID.fromString(id))
                break
            case Event.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = schemeApprovalId
                schemeApprovalService.reject(approverId, cmd, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        show(approverId, schemeApprovalId, id, 'todo')
    }
}
