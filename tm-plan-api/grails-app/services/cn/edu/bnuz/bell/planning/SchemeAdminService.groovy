package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.utils.CollectionUtils
import cn.edu.bnuz.bell.utils.GroupCondition
import grails.transaction.Transactional

@Transactional
class SchemeAdminService {
    SchemePublicService schemePublicService

    def getSchemes(String departmentId, Integer grade) {
        def results = Scheme.executeQuery '''
select new map(
  s.id as id,
  program.id as programId,
  subject.name as subject,
  major.grade as grade,
  s.versionNumber as versionNumber,
  s.status as status
)
from Scheme s
join s.program program
join program.major major
join major.subject subject
join major.department department
join subject.department department
where subject.isTopUp = false
and major.degree is not null
and (:grade = 0 or major.grade = :grade)
and department.id = :departmentId
order by major.grade desc, subject.id, s.versionNumber desc
''', [departmentId: departmentId, grade: grade ?: 0]

        List<GroupCondition> conditions = [
                new GroupCondition(
                        groupBy: 'programId',
                        into: 'versions',
                        mappings: [
                                programId: 'id',
                                subject: 'subject',
                                grade: 'grade',
                        ]
                )
        ]

        CollectionUtils.groupBy(results, conditions)
    }

    def getScheme(Long id) {
        def scheme = schemePublicService.getSchemeInfo(id)

        // 除获取当前指定版本数据外，还需查询出被当前版本修改的项
        if (scheme.previousId) {
            scheme.courses.addAll(schemePublicService.getRevisedSchemeCoursesInfo(id))
            scheme.tempCourses.addAll(schemePublicService.getRevisedSchemeTempCoursesInfo(id))
        }

        return scheme
    }
}
