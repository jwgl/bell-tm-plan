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
 * 教学计划审核服务
 * @author Yang Lin
 */
@Transactional
class SchemeReviewService extends AbstractReviewService {
    SchemePublicService schemePublicService
    SchemeDraftService schemeDraftService
    DomainStateMachineHandler domainStateMachineHandler

    /**
     * 获取审核数据
     * @param id Scheme ID
     * @param userId 用户ID
     * @param workitemId 工作项ID
     * @return 审核数据
     */
    def getSchemeForReview(Long id, String userId, UUID workitemId) {
        def scheme = schemePublicService.getSchemeInfo(id)

        // 除获取当前指定版本数据外，还需查询出被当前版本修改的项
        if (scheme.previousId) {
            scheme.courses.addAll(schemePublicService.getRevisedSchemeCoursesInfo(id))
        }

        def activity = Workitem.get(workitemId).activitySuffix
        checkReviewer(id, activity, userId)
        scheme.activity = activity
        return scheme
    }

    /**
     * 同意
     * @param cmd 同意数据
     * @param userId 用户ID
     * @param workItemId 工作项ID
     */
    void accept(AcceptCommand cmd, String userId, UUID workitemId) {
        Scheme scheme = Scheme.get(cmd.id)

        if (!scheme) {
            throw new NotFoundException()
        }

        if (!domainStateMachineHandler.canAccept(scheme)) {
            throw new BadRequestException()
        }

        def activity = Workitem.get(workitemId).activitySuffix
        checkReviewer(cmd.id, activity, userId)

        domainStateMachineHandler.accept(scheme, userId, cmd.comment, workitemId, cmd.to)

        scheme.save()
    }

    /**
     * 不同意
     * @param cmd 不同意数据
     * @param userId 用户ID
     * @param workItemId 工作项ID
     */
    void reject(RejectCommand cmd, String userId, UUID workitemId) {
        Scheme scheme = Scheme.get(cmd.id)

        if (!scheme) {
            throw new NotFoundException()
        }

        if (!domainStateMachineHandler.canReject(scheme)) {
            throw new BadRequestException()
        }

        def activity = Workitem.get(workitemId).activitySuffix
        checkReviewer(cmd.id, activity, userId)

        domainStateMachineHandler.reject(scheme, userId, cmd.comment, workitemId)

        scheme.save()
    }

    @Override
    List<Map> getReviewers(String activity, Long id) {
        switch (activity) {
            case Activities.CHECK:
                return schemeDraftService.getCheckers(id)
            case Activities.APPROVE:
                return User.findAllWithPermission('PERM_SCHEME_APPROVE')
            default:
                throw new BadRequestException()
        }
    }
}
