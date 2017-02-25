package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 教学计划审核
 * @author Yang Lin
 */
@PreAuthorize('hasAnyAuthority("PERM_SCHEME_CHECK", "PERM_SCHEME_APPROVE")')
class SchemeReviewController implements ServiceExceptionHandler {
    SchemeReviewService schemeReviewService
    SchemeReviewerService schemeReviewerService

    /**
     * 审核显示
     * @param schemeReviewId Scheme ID
     * @param id Workitem ID
     */
    def show(String reviewerId, Long schemeReviewId, String id) {
        renderJson schemeReviewService.getSchemeForReview(reviewerId, schemeReviewId, UUID.fromString(id))
    }

    /**
     * 处理同意/不同意
     * @param schemeReviewId Scheme ID
     * @param id Workitem ID
     * @param op 操作
     * @return
     */
    def patch(String reviewerId, Long schemeReviewId, String id, String op) {
         def operation = Event.valueOf(op)
        switch (operation) {
            case Event.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = schemeReviewId
                schemeReviewService.accept(reviewerId, cmd, UUID.fromString(id))
                break
            case Event.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = schemeReviewId
                schemeReviewService.reject(reviewerId, cmd, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        show(reviewerId, schemeReviewId, id)
    }

    /**
     * 获取批准人
     * @param schemeReviewId Scheme ID
     * @return 批准人列表
     */
    def approvers(String reviewerId, Long schemeReviewId) {
        renderJson schemeReviewerService.getApprovers()
    }
}
