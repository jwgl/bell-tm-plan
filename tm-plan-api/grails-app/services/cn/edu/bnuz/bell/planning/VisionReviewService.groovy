package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.Workitem
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import grails.transaction.Transactional

import javax.annotation.Resource

/**
 * 培养方案审核服务
 * @author Yang Lin
 */
@Transactional
class VisionReviewService {
    VisionPublicService visionPublicService

    @Resource(name='visionStateMachineHandler')
    DomainStateMachineHandler domainStateMachineHandler

    /**
     * 获取审核数据
     * @param userId 用户ID
     * @param id Vision ID
     * @param workitemId 工作项ID
     * @return 审核数据
     */
    def getVisionForReview(String userId, Long id, UUID workitemId) {
        def vision = visionPublicService.getVisionInfo(id)
        def activity = Workitem.get(workitemId).activitySuffix
        domainStateMachineHandler.checkReviewer(id, userId, activity)
        vision.activity = activity
        return vision
    }

    /**
     * 同意
     * @param userId 用户ID
     * @param cmd 同意数据
     * @param workItemId 工作项ID
     */
    void accept(String userId, AcceptCommand cmd, UUID workitemId) {
        Vision vision = Vision.get(cmd.id)
        domainStateMachineHandler.accept(vision, userId, null, cmd.comment, workitemId, cmd.to)
        vision.save()
    }

    /**
     * 不同意
     * @param userId 用户ID
     * @param cmd 不同意数据
     * @param workItemId 工作项ID
     */
    void reject(String userId, RejectCommand cmd, UUID workitemId) {
        Vision vision = Vision.get(cmd.id)
        domainStateMachineHandler.reject(vision, userId, null, cmd.comment, workitemId)
        vision.save()
    }
}
