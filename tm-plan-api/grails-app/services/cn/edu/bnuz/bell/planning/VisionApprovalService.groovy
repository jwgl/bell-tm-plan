package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import grails.transaction.Transactional

@Transactional
class VisionApprovalService extends VisionCheckService {
    protected getCounts(String userId) {
        [
                (ListType.TODO): Vision.countByStatus(State.CHECKED),
                (ListType.DONE): Vision.countByApprover(Teacher.load(userId)),
                (ListType.TOBE): Vision.countByStatus(State.SUBMITTED),
        ]
    }

    def list(String userId, ListCommand cmd) {
        switch (cmd.type) {
            case ListType.TODO:
                return findTodoList(userId, cmd)
            case ListType.DONE:
                return findDoneList(userId, cmd)
            case ListType.TOBE:
                return findTobeList(userId, cmd)
            default:
                throw new BadRequestException()
        }
    }

    def findTodoList(String userId, ListCommand cmd) {
        def visions = Vision.executeQuery '''
select new map(
  vision.id as id,
  vision.versionNumber as versionNumber,
  major.grade as grade,
  department.name as department,
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
where vision.status = :status
order by vision.dateChecked
''', [status: State.CHECKED], [offset: cmd.offset, max: cmd.max]

        [
                forms : visions,
                counts: getCounts(userId)
        ]
    }

    def findDoneList(String userId, ListCommand cmd) {
        def visions = Vision.executeQuery '''
select new map(
  vision.id as id,
  vision.versionNumber as versionNumber,
  department.name as department,
  major.grade as grade,
  subject.name as subject,
  creator.name as creator,
  vision.dateApproved as date,
  vision.status as status
)
from Vision vision
join vision.creator creator
join vision.program program
join program.major major
join major.department department
join major.subject subject
where vision.approver.id = :userId
order by vision.dateApproved desc
''', [userId: userId], [offset: cmd.offset, max: cmd.max]

        [
                forms : visions,
                counts: getCounts(userId)
        ]
    }

    def findTobeList(String userId, ListCommand cmd) {
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
where vision.status = :status
order by vision.dateSubmitted desc
''', [status: State.SUBMITTED], [offset: cmd.offset, max: cmd.max]

        [
                forms: visions,
                counts : getCounts(userId)
        ]
    }

    protected Long getPrevReviewId(String userId, Long id, ListType type) {
        switch (type) {
            case ListType.TODO:
                return dataAccessService.getLong('''
select vision.id
from Vision vision
where vision.status = :status
and vision.dateChecked < (select dateChecked from Vision where id = :id)
order by vision.dateChecked desc
''', [id: id, status: State.CHECKED])
            case ListType.DONE:
                return dataAccessService.getLong('''
select vision.id
from Vision vision
where vision.approver.id = :userId
and vision.dateApproved > (select dateApproved from Vision where id = :id)
order by vision.dateApproved asc
''', [id: id, userId: userId])
            case ListType.TOBE:
                return dataAccessService.getLong('''
select vision.id
from Vision vision
where vision.status = :status
and vision.dateSubmitted > (select dateSubmitted from Vision where id = :id)
order by vision.dateSubmitted asc
''', [id: id, status: State.SUBMITTED])
        }
    }

    protected Long getNextReviewId(String userId, Long id, ListType type) {
        switch (type) {
            case ListType.TODO:
                return dataAccessService.getLong('''
select vision.id
from Vision vision
where vision.status = :status
and vision.dateChecked > (select dateChecked from Vision where id = :id)
order by vision.dateChecked asc
''', [id: id, status: State.CHECKED])
            case ListType.DONE:
                return dataAccessService.getLong('''
select vision.id
from Vision vision
where vision.approver.id = :userId
and vision.dateApproved < (select dateApproved from Vision where id = :id)
order by vision.dateApproved desc
''', [id: id, userId: userId])
            case ListType.TOBE:
                return dataAccessService.getLong('''
select vision.id
from Vision vision
where vision.status = :status
and vision.dateSubmitted < (select dateSubmitted from Vision where id = :id)
order by vision.dateSubmitted desc
''', [id: id, status: State.SUBMITTED])
        }
    }

    void accept(String userId, AcceptCommand cmd, UUID workitemId) {
        Vision vision = Vision.get(cmd.id)
        domainStateMachineHandler.accept(vision, userId, Activities.APPROVE, cmd.comment, workitemId)
        vision.approver = Teacher.load(userId)
        vision.dateApproved = new Date()
        vision.save()
    }

    void reject(String userId, RejectCommand cmd, UUID workitemId) {
        Vision vision = Vision.get(cmd.id)
        domainStateMachineHandler.reject(vision, userId, Activities.APPROVE, cmd.comment, workitemId)
        vision.approver = Teacher.load(userId)
        vision.dateApproved = new Date()
        vision.save()
    }
}
