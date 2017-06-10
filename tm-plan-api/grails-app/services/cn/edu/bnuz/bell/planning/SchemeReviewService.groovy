package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.Workitem
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import grails.gorm.transactions.Transactional

import javax.annotation.Resource

/**
 * 教学计划审核服务
 * @author Yang Lin
 */
@Transactional
class SchemeReviewService {
    SchemePublicService schemePublicService

    @Resource(name='schemeStateMachineHandler')
    DomainStateMachineHandler domainStateMachineHandler

    /**
     * 获取审核数据
     * @param userId 用户ID
     * @param id Scheme ID
     * @param workitemId 工作项ID
     * @return 审核数据
     */
    def getSchemeForReview(String userId, Long id, UUID workitemId) {
        def scheme = schemePublicService.getSchemeInfo(id)

        // 除获取当前指定版本数据外，还需查询出被当前版本修改的项
        if (scheme.previousId) {
            scheme.courses.addAll(schemePublicService.getRevisedSchemeCoursesInfo(id))
            scheme.tempCourses.addAll(schemePublicService.getRevisedSchemeTempCoursesInfo(id))
        }

        def activity = Workitem.get(workitemId).activitySuffix
        domainStateMachineHandler.checkReviewer(id, userId, activity)
        scheme.activity = activity
        return scheme
    }

    /**
     * 同意
     * @param userId 用户ID
     * @param cmd 同意数据
     * @param workItemId 工作项ID
     */
    void accept(String userId, AcceptCommand cmd, UUID workitemId) {
        Scheme scheme = Scheme.get(cmd.id)
        def activity = Workitem.get(workitemId).activitySuffix
        switch (activity) {
            case Activities.CHECK:
                domainStateMachineHandler.accept(scheme, userId, activity, cmd.comment, workitemId, cmd.to)
                break
            case Activities.APPROVE:
                domainStateMachineHandler.accept(scheme, userId, activity, cmd.comment, workitemId)
                break
        }
        scheme.save()
    }

    /**
     * 不同意
     * @param userId 用户ID
     * @param cmd 不同意数据
     * @param workItemId 工作项ID
     */
    void reject(String userId, RejectCommand cmd, UUID workitemId) {
        Scheme scheme = Scheme.get(cmd.id)
        domainStateMachineHandler.reject(scheme, userId, null, cmd.comment, workitemId)
        scheme.save()
    }
}
