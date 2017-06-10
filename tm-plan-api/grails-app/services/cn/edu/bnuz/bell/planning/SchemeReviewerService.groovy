package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.service.DataAccessService
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.ReviewerProvider
import grails.gorm.transactions.Transactional

/**
 * 教学计划审核人服务
 * @author Yang Lin
 */
@Transactional(readOnly = true)
class SchemeReviewerService implements ReviewerProvider{
    DataAccessService dataAccessService

    List<Map> getReviewers(Object id, String activity) {
        switch (activity) {
            case Activities.CHECK:
                return getCheckers(id as Long)
            case Activities.APPROVE:
                return getApprovers()
            default:
                throw new BadRequestException()
        }
    }

    List<Map> getCheckers(Long id) {
        def departmentId = dataAccessService.getString '''
select m.department.id
from Scheme s
join s.program p
join p.major m
where s.id = :id
''', [id: id]
        User.findAllWithPermission('PERM_SCHEME_CHECK', departmentId)
    }

    List<Map> getApprovers() {
        User.findAllWithPermission('PERM_VISION_APPROVE')
    }
}
