package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.Major
import cn.edu.bnuz.bell.organization.Department
import grails.transaction.Transactional

@Transactional
class DepartmentService {
    List<Map> getDepartments() {
        Department.executeQuery '''
SELECT new map(
  department.id as id,
  department.name as name
)
FROM Department department
where department.isTeaching = true
  and department.enabled = true
  and exists (
    select program.id
    from ProgramSettings settings
    join settings.program program
    join program.major major
    where major.department = department 
)
order by id
'''
    }

    List<Integer> getGrades(String departmentId) {
        Major.executeQuery '''
SELECT distinct major.grade
from ProgramSettings settings
    join settings.program program
    join program.major major
where major.department.id = :departmentId
order by major.grade desc
''', [departmentId: departmentId]
    }
}
