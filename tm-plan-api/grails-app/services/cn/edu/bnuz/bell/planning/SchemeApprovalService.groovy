package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import grails.gorm.transactions.Transactional

/**
 * 教学计划审批服务
 * @author Yang Lin
 */
@Transactional
class SchemeApprovalService extends SchemeCheckService {
    protected getCounts(String userId) {
        [
                (ListType.TODO): Scheme.countByStatus(State.CHECKED),
                (ListType.DONE): Scheme.countByApprover(Teacher.load(userId)),
                (ListType.TOBE): Scheme.countByStatus(State.SUBMITTED),
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
        def schemes = Scheme.executeQuery '''
select new map(
  scheme.id as id,
  scheme.versionNumber as versionNumber,
  major.grade as grade,
  department.name as department,
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
where scheme.status = :status
order by scheme.dateChecked
''', [status: State.CHECKED], [offset: cmd.offset, max: cmd.max]

        [
                forms : schemes,
                counts: getCounts(userId)
        ]
    }

    def findDoneList(String userId, ListCommand cmd) {
        def schemes = Scheme.executeQuery '''
select new map(
  scheme.id as id,
  scheme.versionNumber as versionNumber,
  department.name as department,
  major.grade as grade,
  subject.name as subject,
  creator.name as creator,
  scheme.dateApproved as date,
  scheme.status as status
)
from Scheme scheme
join scheme.creator creator
join scheme.program program
join program.major major
join major.department department
join major.subject subject
where scheme.approver.id = :userId
order by scheme.dateApproved desc
''', [userId: userId], [offset: cmd.offset, max: cmd.max]

        [
                forms : schemes,
                counts: getCounts(userId)
        ]
    }

    def findTobeList(String userId, ListCommand cmd) {
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
where scheme.status = :status
order by scheme.dateSubmitted desc
''', [status: State.SUBMITTED], [offset: cmd.offset, max: cmd.max]

        [
                forms: schemes,
                counts : getCounts(userId)
        ]
    }

    protected Long getPrevReviewId(String userId, Long id, ListType type) {
        switch (type) {
            case ListType.TODO:
                return dataAccessService.getLong('''
select scheme.id
from Scheme scheme
where scheme.status = :status
and scheme.dateChecked < (select dateChecked from Scheme where id = :id)
order by scheme.dateChecked desc
''', [id: id, status: State.CHECKED])
            case ListType.DONE:
                return dataAccessService.getLong('''
select scheme.id
from Scheme scheme
where scheme.approver.id = :userId
and scheme.dateApproved > (select dateApproved from Scheme where id = :id)
order by scheme.dateApproved asc
''', [id: id, userId: userId])
            case ListType.TOBE:
                return dataAccessService.getLong('''
select scheme.id
from Scheme scheme
where scheme.status = :status
and scheme.dateSubmitted > (select dateSubmitted from Scheme where id = :id)
order by scheme.dateSubmitted asc
''', [id: id, status: State.SUBMITTED])
        }
    }

    protected Long getNextReviewId(String userId, Long id, ListType type) {
        switch (type) {
            case ListType.TODO:
                return dataAccessService.getLong('''
select scheme.id
from Scheme scheme
where scheme.status = :status
and scheme.dateChecked > (select dateChecked from Scheme where id = :id)
order by scheme.dateChecked asc
''', [id: id, status: State.CHECKED])
            case ListType.DONE:
                return dataAccessService.getLong('''
select scheme.id
from Scheme scheme
where scheme.approver.id = :userId
and scheme.dateApproved < (select dateApproved from Scheme where id = :id)
order by scheme.dateApproved desc
''', [id: id, userId: userId])
            case ListType.TOBE:
                return dataAccessService.getLong('''
select scheme.id
from Scheme scheme
where scheme.status = :status
and scheme.dateSubmitted < (select dateSubmitted from Scheme where id = :id)
order by scheme.dateSubmitted desc
''', [id: id, status: State.SUBMITTED])
        }
    }

    void accept(String userId, AcceptCommand cmd, UUID workitemId) {
        Scheme scheme = Scheme.get(cmd.id)
        domainStateMachineHandler.accept(scheme, userId, Activities.APPROVE, cmd.comment, workitemId)
        scheme.approver = Teacher.load(userId)
        scheme.dateApproved = new Date()
        scheme.save()
    }

    void reject(String userId, RejectCommand cmd, UUID workitemId) {
        Scheme scheme = Scheme.get(cmd.id)
        domainStateMachineHandler.reject(scheme, userId, Activities.APPROVE, cmd.comment, workitemId)
        scheme.approver = Teacher.load(userId)
        scheme.dateApproved = new Date()
        scheme.save()
    }
}
