package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.workflow.State
import grails.transaction.Transactional

@Transactional
class SchemeDepartmentService {
    TermService termService
    SchemePublicService schemePublicService

    def getSchemes(String departmentId) {
        Scheme.executeQuery '''
select new map(
  s.id as id,
  subject.name as subjectName,
  major.grade as grade,
  s.versionNumber as versionNumber,
  s.status as status
)
from Scheme s
join s.program program
join program.major major
join major.subject subject
join subject.department department
where subject.isTopUp = false
and major.degree is not null
and department.id = :departmentId
order by major.grade desc, subject.id, s.versionNumber desc
''', [departmentId: departmentId]
    }

    def getScheme(String departmentId, Long id) {
        def scheme = schemePublicService.getSchemeInfo(id)

        if (scheme.departmentId != departmentId) {
            throw new ForbiddenException()
        }

        // 除获取当前指定版本数据外，还需查询出被当前版本修改的项
        if (scheme.previousId) {
            scheme.courses.addAll(schemePublicService.getRevisedSchemeCoursesInfo(id))
            scheme.tempCourses.addAll(schemePublicService.getRevisedSchemeTempCoursesInfo(id))
        }

        return scheme
    }

    /**
     * 按学院获取最新版列表
     * @param departmentId 学院ID
     */
    def getLatestSchemes(String departmentId) {
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
    def getLatestDirections(String departmentId) {
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
