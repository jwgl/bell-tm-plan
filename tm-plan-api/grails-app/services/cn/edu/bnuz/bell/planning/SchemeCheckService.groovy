package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.service.DataAccessService
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.WorkflowActivity
import cn.edu.bnuz.bell.workflow.WorkflowInstance
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
class SchemeCheckService {
    SchemePublicService schemePublicService
    @Resource(name = 'schemeStateMachineHandler')
    DomainStateMachineHandler domainStateMachineHandler
    DataAccessService dataAccessService

    protected getCounts(String userId) {
        def todo = dataAccessService.getInteger '''
select count(*)
from Scheme scheme
join scheme.program program
join program.major major
join major.department department
join major.subject subject
where department = (
  select teacher.department
  from Teacher teacher
  where teacher.id = :userId
) and scheme.status = :status
''', [userId: userId, status: State.SUBMITTED]

        def done = Scheme.countByChecker(Teacher.load(userId))

        [
                (ListType.TODO): todo,
                (ListType.DONE): done,
        ]
    }

    def list(String userId, ListCommand cmd) {
        switch (cmd.type) {
            case ListType.TODO:
                return findTodoList(userId, cmd.args)
            case ListType.DONE:
                return findDoneList(userId, cmd.args)
            default:
                throw new BadRequestException()
        }
    }

    def findTodoList(String userId, Map args) {
        def schemes = Scheme.executeQuery '''
select new map(
  scheme.id as id,
  scheme.versionNumber as versionNumber,
  department.name as department,
  major.grade as grade,
  subject.name as subject,
  creator.name as creator,
  scheme.dateSubmitted as date,
  scheme.status as status
)
from Scheme scheme
join scheme.creator creator
join scheme.program program
join program.major major
join major.department department
join major.subject subject
where department = (
  select teacher.department
  from Teacher teacher
  where teacher.id = :userId
) and scheme.status = :status
order by scheme.dateSubmitted
''', [userId: userId, status: State.SUBMITTED], args

        [
                forms : schemes,
                counts: getCounts(userId)
        ]
    }

    def findDoneList(String userId, Map args) {
        def schemes = Scheme.executeQuery '''
select new map(
  scheme.id as id,
  scheme.versionNumber as versionNumber,
  department.name as department,
  major.grade as grade,
  subject.name as subject,
  creator.name as creator,
  scheme.dateChecked as date,
  scheme.status as status
)
from Scheme scheme
join scheme.creator creator
join scheme.program program
join program.major major
join major.department department
join major.subject subject
where scheme.checker.id = :userId
order by scheme.dateChecked desc
''', [userId: userId], args

        [
                forms : schemes,
                counts: getCounts(userId)
        ]
    }

    def getSchemeForReview(String userId, Long id, ListType type, String activity) {
        def scheme = schemePublicService.getSchemeInfo(id)

        // 除获取当前指定版本数据外，还需查询出被当前版本修改的项
        if (scheme.previousId) {
            scheme.courses.addAll(schemePublicService.getRevisedSchemeCoursesInfo(id))
            scheme.tempCourses.addAll(schemePublicService.getRevisedSchemeTempCoursesInfo(id))
        }

        def workitem = Workitem.findByInstanceAndActivityAndToAndDateProcessedIsNull(
                WorkflowInstance.load(scheme.workflowInstanceId),
                WorkflowActivity.load("${scheme.previousId ? Scheme.REVISE_WORKFLOW_ID : Scheme.CREATE_WORKFLOW_ID}.${activity}"),
                User.load(userId),
        )

        domainStateMachineHandler.checkReviewer(id, userId, activity)

        [
                scheme    : scheme,
                counts    : getCounts(userId),
                workitemId: workitem ? workitem.id : null,
                prevId    : getPrevReviewId(userId, id, type),
                nextId    : getNextReviewId(userId, id, type),
        ]
    }

    def getSchemeForReview(String userId, Long id, ListType type, UUID workitemId) {
        def scheme = schemePublicService.getSchemeInfo(id)

        // 除获取当前指定版本数据外，还需查询出被当前版本修改的项
        if (scheme.previousId) {
            scheme.courses.addAll(schemePublicService.getRevisedSchemeCoursesInfo(id))
            scheme.tempCourses.addAll(schemePublicService.getRevisedSchemeTempCoursesInfo(id))
        }

        def activity = Workitem.get(workitemId).activitySuffix

        domainStateMachineHandler.checkReviewer(id, userId, activity)

        [
                scheme    : scheme,
                counts    : getCounts(userId),
                workitemId: workitemId,
                prevId    : getPrevReviewId(userId, id, type),
                nextId    : getNextReviewId(userId, id, type),
        ]
    }

    protected Long getPrevReviewId(String userId, Long id, ListType type) {
        switch (type) {
            case ListType.TODO:
                return dataAccessService.getLong('''
select scheme.id
from Scheme scheme
join scheme.program program
join program.major major
join major.department department
join major.subject subject
where department = (
  select teacher.department
  from Teacher teacher
  where teacher.id = :userId
) and scheme.status = :status
and scheme.dateSubmitted < (select dateSubmitted from Scheme where id = :id)
order by scheme.dateSubmitted desc
''', [userId: userId, id: id, status: State.SUBMITTED])
            case ListType.DONE:
                return dataAccessService.getLong('''
select scheme.id
from Scheme scheme
where scheme.checker.id = :userId
and scheme.dateChecked > (select dateChecked from Scheme where id = :id)
order by scheme.dateChecked asc
''', [userId: userId, id: id])
        }
    }

    protected Long getNextReviewId(String userId, Long id, ListType type) {
        switch (type) {
            case ListType.TODO:
                return dataAccessService.getLong('''
select scheme.id
from Scheme scheme
join scheme.program program
join program.major major
join major.department department
join major.subject subject
where department = (
  select teacher.department
  from Teacher teacher
  where teacher.id = :userId
) and scheme.status = :status
and scheme.dateSubmitted > (select dateSubmitted from Scheme where id = :id)
order by scheme.dateSubmitted asc
''', [userId: userId, id: id, status: State.SUBMITTED])
            case ListType.DONE:
                return dataAccessService.getLong('''
select scheme.id
from Scheme scheme
where scheme.checker.id = :userId
and scheme.dateChecked < (select dateChecked from Scheme where id = :id)
order by scheme.dateChecked desc
''', [userId: userId, id: id])
        }
    }

    void accept(String userId, AcceptCommand cmd, UUID workitemId) {
        Scheme scheme = Scheme.get(cmd.id)
        domainStateMachineHandler.accept(scheme, userId, Activities.CHECK, cmd.comment, workitemId, cmd.to)
        scheme.checker = Teacher.load(userId)
        scheme.dateChecked = new Date()
        scheme.save()
    }

    void reject(String userId, RejectCommand cmd, UUID workitemId) {
        Scheme scheme = Scheme.get(cmd.id)
        domainStateMachineHandler.reject(scheme, userId, Activities.CHECK, cmd.comment, workitemId)
        scheme.checker = Teacher.load(userId)
        scheme.dateChecked = new Date()
        scheme.save()
    }
}
