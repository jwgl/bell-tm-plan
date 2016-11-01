package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.workflow.AbstractReviewService
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.Workitem
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import grails.transaction.Transactional

/**
 * 培养方案审核服务
 * @author Yang Lin
 */
@Transactional
class VisionReviewService extends AbstractReviewService {
    VisionPublicService visionPublicService
    VisionDraftService visionDraftService
    DomainStateMachineHandler domainStateMachineHandler

    /**
     * 获取审核数据
     * @param id Vision ID
     * @param userId 用户ID
     * @param workitemId 工作项ID
     * @return 审核数据
     */
    def getVisionForReview(Long id, String userId, UUID workitemId) {
        def vision = visionPublicService.getVisionInfo(id)
        def activity = Workitem.get(workitemId).activitySuffix
        checkReviewer(id, activity, userId)
        vision.activity = activity
        return vision
    }

    /**
     * 同意
     * @param cmd 同意数据
     * @param userId 用户ID
     * @param workItemId 工作项ID
     */
    void accept(AcceptCommand cmd, String userId, UUID workitemId) {
        Vision vision = Vision.get(cmd.id)

        if (!vision) {
            throw new NotFoundException()
        }

        if (!domainStateMachineHandler.canAccept(vision)) {
            throw new BadRequestException()
        }

        def activity = Workitem.get(workitemId).activitySuffix
        checkReviewer(cmd.id, activity, userId)

        domainStateMachineHandler.accept(vision, userId, cmd.comment, workitemId, cmd.to)

        vision.save()
    }

    /**
     * 不同意
     * @param cmd 不同意数据
     * @param userId 用户ID
     * @param workItemId 工作项ID
     */
    void reject(RejectCommand cmd, String userId, UUID workitemId) {
        Vision vision = Vision.get(cmd.id)

        if (!vision) {
            throw new NotFoundException()
        }

        if (!domainStateMachineHandler.canReject(vision)) {
            throw new BadRequestException()
        }

        def activity = Workitem.get(workitemId).activitySuffix
        checkReviewer(cmd.id, activity, userId)

        domainStateMachineHandler.reject(vision, userId, cmd.comment, workitemId)

        vision.save()
    }

    @Override
    List<Map> getReviewers(String activity, Long id) {
        switch (activity) {
            case Activities.CHECK:
                return visionDraftService.getCheckers(id)
            case Activities.APPROVE:
                return User.findAllWithPermission('PERM_VISION_APPROVE')
            default:
                throw new BadRequestException()
        }
    }
}
