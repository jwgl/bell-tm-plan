package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.workflow.State
import grails.transaction.Transactional

@Transactional
class VisionDepartmentService {
    TermService termService

    /**
     * 按学院获取列表
     * @param departmentId 学院ID
     */
    def getLatestVisions(String departmentId) {
        def startGrade = termService.minInSchoolGrade
        Vision.executeQuery '''
select new map(
  v.id as id,
  subject.name as subjectName,
  major.grade as grade
)
from Vision v
join v.program program
join program.major major
join major.subject subject
join subject.department department
where subject.isTopUp = false
and major.degree is not null
and major.grade >= :startGrade
and department.id = :departmentId
and v.versionNumber = (
  select max(v2.versionNumber)
  from Vision v2
  where v2.status = :status
  and v2.program = v.program
)
order by major.grade desc, subject.id
''', [startGrade: startGrade, departmentId: departmentId, status: State.APPROVED]
    }
}
