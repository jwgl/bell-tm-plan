package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import grails.transaction.Transactional

@Transactional
class SchemeToesService {

    def getProgramCourses(Integer programId) {
        ProgramCourseDto.executeQuery '''
select new map(
    dto.courseId as courseId,
    dto.directionId as directionId,
    dto.startWeek as startWeek,
    dto.endWeek as endWeek,
    dto.departmentId as departmentId,
    dept.name as departmentName
)
from ProgramCourseDto dto, Department dept
where dto.departmentId = dept.id
and dto.programId = :programId
''', [programId: programId]
    }

    def save(String departmentId, Long schemeId, ProgramCoursesCommand cmd) {
        List<Map> results = Scheme.executeQuery '''
select new map(
  major.department.id as departmentId,
  program.id as programId
)
from Scheme scheme
join scheme.program program
join program.major major
where scheme.id = :schemeId
''', [schemeId: schemeId]

        if (!results) {
            throw new NotFoundException()
        }

        def schemeInfo = results[0]
        if (schemeInfo.departmentId != departmentId) {
            throw new ForbiddenException()
        }

        cmd.courses.each { programCourse ->
            ProgramCourseEto.executeUpdate '''
insert into ProgramCourseEto(
  programId, courseId, period, propertyId, assessType, testType,
  startWeek, endWeek, suggestedTerm, allowedTerm, departmentId, directionId)
select :programId, sc.course.id, sc.period, sc.property.id, sc.assessType, :testType,
  :startWeek, :endWeek, sc.suggestedTerm, sc.allowedTerm, :departmentId, direction.id
from SchemeCourse sc
left join sc.direction direction
where sc.id = :schemeCourseId
''', [programId: schemeInfo.programId,
      schemeCourseId: programCourse.schemeCourseId,
      testType: programCourse.testType,
      startWeek: programCourse.startWeek,
      endWeek: programCourse.endWeek,
      departmentId: programCourse.departmentId]
        }
    }
}
