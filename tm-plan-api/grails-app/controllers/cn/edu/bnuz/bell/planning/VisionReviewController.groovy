package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ServiceExceptionHandler
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
    VisionReviewService visionReviewService

    /**
     * 审核显示
     * @param reviewerId Reviewer ID
     * @param visionReviewId Vision ID
     * @param id Workitem ID
     */
    def show(String reviewerId, Long visionReviewId, String id) {
        renderJson visionReviewService.getVisionForReview(reviewerId, visionReviewId, UUID.fromString(id))
    }

    /**
     * 处理同意/不同意
     * @param reviewerId Reviewer ID
     * @param visionReviewId Vision ID
     * @param id Workitem ID
     * @param op 操作
     */
    def patch(String reviewerId, Long visionReviewId, String id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = visionReviewId
                visionReviewService.accept(reviewerId, cmd, UUID.fromString(id))
                break
            case Event.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = visionReviewId
                visionReviewService.reject(reviewerId, cmd, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        show(reviewerId, visionReviewId, id)
    }

    /**
     * 获取批准人
     * @param reviewerId Reviewer ID
     * @param visionReviewId Vision ID
     * @return 批准人列表
     */
    def approvers(String reviewerId, Long visionReviewId) {
        renderJson visionReviewService.getReviewers(Activities.APPROVE, visionReviewId)
    }
}
