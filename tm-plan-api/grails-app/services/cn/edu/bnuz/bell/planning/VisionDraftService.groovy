package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.security.UserLogService
import cn.edu.bnuz.bell.service.DataAccessService
import cn.edu.bnuz.bell.utils.CollectionUtils
import cn.edu.bnuz.bell.utils.GroupCondition
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.WorkflowService
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import grails.transaction.Transactional

/**
 * 培养方案编辑服务
 * @author Yang Lin
 */
@Transactional
class VisionDraftService {
    ProgramService programService
    SchemePublicService schemePublicService
    VisionPublicService visionPublicService
    UserLogService userLogService
    WorkflowService workflowService
    DataAccessService dataAccessService
    TermService termService
    DomainStateMachineHandler domainStateMachineHandler

    /**
     * 获取所有者的培养方案
     * @param userId
     * @return 培养方案列表
     */
    def getVisions(String userId) {
        def startGrade = termService.minInSchoolGrade
        def results = Vision.executeQuery '''
select new map(
  v.id as id,
  program.id as programId,
  subject.id as subjectId,
  program.type as programType,
  subject.name as subjectName,
  major.grade as grade,
  v.versionNumber as versionNumber,
  v.status as status
)
from Program program
join program.major major
join major.subject subject
left join Vision v on v.program.id = program.id
where subject.isTopUp = false
and major.degree is not null
and major.grade >= :startGrade
and subject.id in (
  select sd.subject.id
  from SubjectDirector sd
  where sd.teacher.id = :userId
)
order by subject.id, major.grade desc, v.versionNumber desc
''', [startGrade: startGrade, userId: userId]

        List<GroupCondition> conditions = [
                new GroupCondition(
                        groupBy: 'subjectId',
                        into: 'programs',
                        mappings: [
                                subjectId  : 'id',
                                subjectName: 'name',
                        ]
                ),
                new GroupCondition(
                        groupBy: 'programId',
                        into: 'visions',
                        mappings: [
                                programId  : 'id',
                                programType: 'type',
                                grade: 'grade',
                        ]
                )
        ]

        CollectionUtils.groupBy(results, conditions)
    }

    /**
     * 获取显示数据
     * @param id Vision ID
     * @param userId 用户ID
     * @return 显示数据
     */
    def getVisionForShow(Long id, String userId) {
        def vision = visionPublicService.getVisionInfo(id)

        if (!programService.isOwner((Integer)vision.programId, userId)) {
            throw new ForbiddenException()
        }

        if (domainStateMachineHandler.canUpdate(vision)) {
            vision.editable = true
        } else if (canRevise(id)) {
            vision.revisable = true
        }

        return vision
    }

    /**
     * 获取编辑数据
     * @param id Vision ID
     * @param userId 用户ID
     * @return 编辑数据
     */
    def getVisionForEdit(Long id, String userId) {
        def vision = visionPublicService.getVisionInfo(id)

        if (!programService.isOwner((Integer)vision.programId, userId)) {
            throw new ForbiddenException()
        }

        if (!domainStateMachineHandler.canUpdate(vision)) {
            throw new BadRequestException()
        }

        return vision
    }

    def getVisionForRevise(Long base, String userId) {
        def vision = visionPublicService.getVisionInfo(base)

        if (!programService.isOwner((Integer)vision.programId, userId)) {
            throw new ForbiddenException()
        }

        if (!canRevise(base)) {
            throw new BadRequestException()
        }

        vision.id = null
        vision.previousId = base
        vision.previousVersionNumber = vision.versionNumber
        vision.versionNumber += Vision.VERSION_INCREMENT

        return vision
    }

    def getVisionForCreate(Integer programId, String userId) {
        List<Map> results = Program.executeQuery '''
select new map(
  program.id as programId,
  program.type as programType,
  subject.name as subjectName,
  department.id as departmentId,
  department.name as departmentName,
  major.grade as grade
)
from Program program
join program.major major
join major.subject subject
join major.department department
where program.id = :programId
''', [programId: programId]

        if(!results) {
            throw new NotFoundException()
        }

        Map vision = results[0]
        if (!programService.isOwner(programId, userId)) {
            throw new ForbiddenException()
        }

        if (!canCreate(programId)) {
            throw new BadRequestException()
        }

        vision.id = null
        vision.previousId = null
        vision.previousVersionNumber = 0
        vision.versionNumber = Vision.INITIAL_VERSION
        vision.schemeId = schemePublicService.getLatestSchemeId(programId)

        return vision
    }

    /**
     * 新建
     * @param cmd 新建数据
     * @param userId 用户ID
     * @return Vision
     */
    Vision create(VisionCreateCommand cmd, String userId) {
        if (!programService.isOwner(cmd.programId, userId)) {
            throw new ForbiddenException()
        }

        if (!canCreate(cmd.programId)) {
            throw new BadRequestException()
        }

        Vision vision = new Vision()
        vision.properties = cmd
        vision.program = Program.load(cmd.programId)
        vision.status = domainStateMachineHandler.initialState
        vision.save()

        domainStateMachineHandler.create(vision, userId)

        return vision
    }

    /**
     * 修订
     * @param cmd 修订数据
     * @param userId 用户ID
     * @return Vision
     */
    Vision revise(VisionReviseCommand cmd, String userId) {
        if (!programService.isOwner(cmd.programId, userId)) {
            throw new ForbiddenException()
        }

        Vision previous = Vision.get(cmd.previousId)
        if (!previous || !canRevise(cmd.previousId) || cmd.versionNumber <= previous.versionNumber) {
            throw new BadRequestException()
        }

        Vision vision = new Vision()
        vision.properties = cmd
        vision.program = previous.program
        vision.previous = previous
        vision.status = domainStateMachineHandler.initialState
        vision.save()

        domainStateMachineHandler.create(vision, userId)

        return vision
    }

    /**
     * 更新
     * @param cmd 更新数据
     * @param userId 用户ID
     * @return Vision
     */
    def update(VisionUpdateCommand cmd, String userId) {
        Vision vision = Vision.get(cmd.id)

        if (!vision) {
            throw new NotFoundException()
        }

        if (!programService.isOwner(vision.program.id, userId)) {
            throw new ForbiddenException()
        }

        if (!domainStateMachineHandler.canUpdate(vision)) {
            throw new BadRequestException()
        }

        vision.properties = cmd

        domainStateMachineHandler.update(vision, userId)

        vision.save()
    }

    /**
     * 删除
     * @param id Vision ID
     * @param userId 用户ID
     */
    void delete(Long id, String userId) {
        Vision vision = Vision.get(id)

        if (!vision) {
            throw new NotFoundException()
        }

        if (!programService.isOwner(vision.program.id, userId)) {
            throw new ForbiddenException()
        }

        if (!domainStateMachineHandler.canUpdate(vision)) {
            throw new BadRequestException()
        }

        if (vision.workflowInstance) {
            vision.workflowInstance.delete()
        }

        vision.delete()
    }

    /**
     * 提交审核
     * @param cmd 命令
     * @param userId 提交人
     */
    void submit(SubmitCommand cmd, String userId) {
        Vision vision = Vision.get(cmd.id)

        if (!vision) {
            throw new NotFoundException()
        }

        if (!programService.isOwner(vision.program.id, userId)) {
            throw new ForbiddenException()
        }

        if (!domainStateMachineHandler.canSubmit(vision)) {
            throw new BadRequestException()
        }

        domainStateMachineHandler.submit(vision, userId, cmd.to, cmd.comment, cmd.title)

        vision.save()
    }

    /**
     * 是否可修订。只有当前记录是最新版本且通过审批，才可以修订。
     * @param id 培养方案ID
     * @return 是否可修订
     */
    boolean canRevise(Long id) {
        dataAccessService.exists '''
select vision.id
from Vision vision
where vision.id = :id
  and vision.status = :status
  and vision.versionNumber = (
    select max(v.versionNumber)
    from Vision v
    where v.program = vision.program
  )
  and vision.program.id in (
    select program.id
    from ProgramSettings ps
    where ps.visionRevisible = true
  )
''', [id: id, status: State.APPROVED]
    }

    /**
     * 是否可创建，只有不存在记录时，才可以创建
     * @param programId Program ID
     * @return 是否可创建
     */
    boolean canCreate(Integer programId) {
        Vision.countByProgram(Program.load(programId)) == 0
    }

    /**
     * 获取审核人
     * @param id Vision ID
     * @return 审核人列表
     */
    List getCheckers(Long id) {
        def departmentId = dataAccessService.getString '''
select m.department.id
from Vision v
join v.program p
join p.major m
where v.id = :id
''', [id: id]
        User.findAllWithPermission('PERM_VISION_CHECK', departmentId)
    }
}