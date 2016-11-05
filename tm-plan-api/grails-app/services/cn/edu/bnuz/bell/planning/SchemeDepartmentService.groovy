package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.tm.common.master.TermService
import cn.edu.bnuz.bell.workflow.State
import grails.transaction.Transactional

@Transactional
class SchemeDepartmentService {
    TermService termService

    /**
     * 按学院获取列表
     * @param departmentId 学院ID
     */
    def getSchemesByDepartment(String departmentId) {
        def startGrade = termService.minInSchoolGrade
        Scheme.executeQuery '''
select new map(
  s.id as id,
  subject.name as subjectName,
  major.grade as grade
)
from Scheme s
join s.program program
join program.major major
join major.subject subject
join subject.department department
where subject.isTopUp = false
and major.degree is not null
and major.grade >= :startGrade
and department.id = :departmentId
and s.versionNumber = (
  select max(s2.versionNumber)
  from Scheme s2
  where s2.status = :status
  and s2.program = s.program
)
order by major.grade desc, subject.id
''', [startGrade: startGrade, departmentId: departmentId, status: State.APPROVED]
    }

    /**
     * 获取指定部门最新Scheme版本中包含的专业方向
     * @param departmentId
     * @return 方向列表
     */
    def getDirectionsByDepartment(String departmentId) {
        def startGrade = termService.minInSchoolGrade
        Scheme.executeQuery '''
select new map(
  s.id as schemeId,
  subject.name as subjectName,
  major.grade as grade,
  direction.id as directionId,
  direction.name as directionName
)
from Scheme s
join s.program program
join program.directions direction
join program.major major
join major.subject subject
join major.department department
where subject.isTopUp = false
and major.degree is not null
and major.grade >= :startGrade
and department.id = :departmentId
and s.versionNumber = (
  select max(s2.versionNumber)
  from Scheme s2
  where s2.status = :status
  and s2.program = s.program
)
order by major.grade desc, subject.id
''', [startGrade: startGrade, departmentId: departmentId, status: State.APPROVED]
    }
}
