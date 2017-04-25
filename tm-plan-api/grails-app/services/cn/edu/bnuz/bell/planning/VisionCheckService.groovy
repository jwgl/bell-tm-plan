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
import grails.transaction.Transactional

import javax.annotation.Resource

@Transactional
class VisionCheckService {
    VisionPublicService visionPublicService
    @Resource(name='visionStateMachineHandler')
    DomainStateMachineHandler domainStateMachineHandler
    DataAccessService dataAccessService

    protected getCounts(String userId) {
        def todo = dataAccessService.getInteger '''
select count(*)
from Vision vision
join vision.program program
join program.major major
join major.department department
join major.subject subject
where department = (
  select teacher.department
  from Teacher teacher
  where teacher.id = :userId
) and vision.status = :status
''', [userId: userId, status: State.SUBMITTED]

        def done = Vision.countByChecker(Teacher.load(userId))

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
        def visions = Vision.executeQuery '''
select new map(
  vision.id as id,
  vision.versionNumber as versionNumber,
  department.name as department,
  major.grade as grade,
  subject.name as subject,
  creator.name as creator,
  vision.dateSubmitted as date,
  vision.status as status
)
from Vision vision
join vision.creator creator
join vision.program program
join program.major major
join major.department department
join major.subject subject
where department = (
  select teacher.department
  from Teacher teacher
  where teacher.id = :userId
) and vision.status = :status
order by vision.dateSubmitted
''', [userId: userId, status: State.SUBMITTED], args

        [
                forms: visions,
                counts: getCounts(userId)
        ]
    }

    def findDoneList(String userId, Map args) {
        def visions = Vision.executeQuery '''
select new map(
  vision.id as id,
  vision.versionNumber as versionNumber,
  department.name as department,
  major.grade as grade,
  subject.name as subject,
  creator.name as creator,
  vision.dateChecked as date,
  vision.status as status
)
from Vision vision
join vision.creator creator
join vision.program program
join program.major major
join major.department department
join major.subject subject
where vision.checker.id = :userId
order by vision.dateChecked desc
''', [userId: userId], args

        [
                forms: visions,
                counts: getCounts(userId)
        ]
    }

    def getVisionForReview(String userId, Long id, ListType type, String activity) {
        def vision = visionPublicService.getVisionInfo(id)

        def workitem = Workitem.findByInstanceAndActivityAndToAndDateProcessedIsNull(
                WorkflowInstance.load(vision.workflowInstanceId),
                WorkflowActivity.load("${vision.previousId ? Vision.REVISE_WORKFLOW_ID: Vision.CREATE_WORKFLOW_ID}.${activity}"),
                User.load(userId),
        )

        domainStateMachineHandler.checkReviewer(id, userId, activity)

        [
                vision: vision,
                counts: getCounts(userId),
                workitemId: workitem ? workitem.id : null,
                prevId: getPrevReviewId(userId, id, type),
                nextId: getNextReviewId(userId, id, type),
        ]
    }

    def getVisionForReview(String userId, Long id, ListType type, UUID workitemId) {
        def vision = visionPublicService.getVisionInfo(id)

        def activity = Workitem.get(workitemId).activitySuffix

        domainStateMachineHandler.checkReviewer(id, userId, activity)

        [
                vision: vision,
                counts: getCounts(userId),
                workitemId: workitemId,
                prevId: getPrevReviewId(userId, id, type),
                nextId: getNextReviewId(userId, id, type),
        ]
    }

    protected Long getPrevReviewId(String userId, Long id, ListType type) {
        switch (type) {
            case ListType.TODO:
                return dataAccessService.getLong('''
select vision.id
from Vision vision
join vision.program program
join program.major major
join major.department department
join major.subject subject
where department = (
  select teacher.department
  from Teacher teacher
  where teacher.id = :userId
) and vision.status = :status
and vision.dateSubmitted < (select dateSubmitted from Vision where id = :id)
order by vision.dateSubmitted desc
''', [userId: userId, id: id, status: State.SUBMITTED])
            case ListType.DONE:
                return dataAccessService.getLong('''
select vision.id
from Vision vision
where vision.checker.id = :userId
and vision.dateChecked > (select dateChecked from Vision where id = :id)
order by vision.dateChecked asc
''', [userId: userId, id: id])
        }
    }

    protected Long getNextReviewId(String userId, Long id, ListType type) {
        switch (type) {
            case ListType.TODO:
                return dataAccessService.getLong('''
select vision.id
from Vision vision
join vision.program program
join program.major major
join major.department department
join major.subject subject
where department = (
  select teacher.department
  from Teacher teacher
  where teacher.id = :userId
) and vision.status = :status
and vision.dateSubmitted > (select dateSubmitted from Vision where id = :id)
order by vision.dateSubmitted asc
''', [userId: userId, id: id, status: State.SUBMITTED])
            case ListType.DONE:
                return dataAccessService.getLong('''
select vision.id
from Vision vision
where vision.checker.id = :userId
and vision.dateChecked < (select dateChecked from Vision where id = :id)
order by vision.dateChecked desc
''', [userId: userId, id: id])
        }
    }

    void accept(String userId, AcceptCommand cmd, UUID workitemId) {
        Vision vision = Vision.get(cmd.id)
        domainStateMachineHandler.accept(vision, userId, Activities.CHECK, cmd.comment, workitemId, cmd.to)
        vision.checker = Teacher.load(userId)
        vision.dateChecked = new Date()
        vision.save()
    }

    void reject(String userId, RejectCommand cmd, UUID workitemId) {
        Vision vision = Vision.get(cmd.id)
        domainStateMachineHandler.reject(vision, userId, Activities.CHECK, cmd.comment, workitemId)
        vision.checker = Teacher.load(userId)
        vision.dateChecked = new Date()
        vision.save()
    }
}
