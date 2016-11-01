package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 培养方案审核控制器。
 * @author Yang Lin
 */
@PreAuthorize('hasAnyAuthority("PERM_VISION_CHECK", "PERM_VISION_APPROVE")')
class VisionReviewController implements ServiceExceptionHandler {
    SecurityService securityService
    VisionReviewService visionReviewService

    /**
     * 审核显示
     * @param visionPublicId Vision ID
     * @param id Workitem ID
     */
    def show(Long visionPublicId, String id) {
        renderJson visionReviewService.getVisionForReview(visionPublicId, securityService.userId, UUID.fromString(id))
    }

    /**
     * 处理同意/不同意
     * @param visionPublicId Vision ID
     * @param id Workitem ID
     * @param op 操作
     */
    def patch(Long visionPublicId, String id, String op) {
        def userId = securityService.userId
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = visionPublicId
                visionReviewService.accept(cmd, userId, UUID.fromString(id))
                break
            case Event.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = visionPublicId
                visionReviewService.reject(cmd, userId, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        renderOk()
    }

    /**
     * 获取批准人
     * @param visionPublicId Vision ID
     * @return 批准人列表
     */
    def approvers(Long visionPublicId) {
        renderJson visionReviewService.getReviewers(Activities.APPROVE, visionPublicId)
    }
}
