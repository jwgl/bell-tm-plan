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

@PreAuthorize('hasAuthority("PERM_VISION_CHECK")')
class VisionCheckController implements ServiceExceptionHandler {
    VisionCheckService visionCheckService
    VisionReviewerService visionReviewerService

    def index(String checkerId, ListCommand cmd) {
        renderJson visionCheckService.list(checkerId, cmd)
    }

    def show(String checkerId, Long visionCheckId, String id, String type) {
        ListType listType= ListType.valueOf(type)
        if (id == 'undefined') {
            renderJson visionCheckService.getVisionForReview(checkerId, visionCheckId, listType, Activities.CHECK)
        } else {
            renderJson visionCheckService.getVisionForReview(checkerId, visionCheckId, listType, UUID.fromString(id))
        }
    }

    def patch(String checkerId, Long visionCheckId, String id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = visionCheckId
                visionCheckService.accept(checkerId, cmd, UUID.fromString(id))
                break
            case Event.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = visionCheckId
                visionCheckService.reject(checkerId, cmd, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        show(checkerId, visionCheckId, id, 'todo')
    }

    def approvers(String checkerId, Long visionCheckId) {
        renderJson visionReviewerService.getApprovers()
    }
}
