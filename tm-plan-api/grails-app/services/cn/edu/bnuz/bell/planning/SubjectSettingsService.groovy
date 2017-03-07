package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.Subject
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.master.TermService
import grails.transaction.Transactional

@Transactional
class SubjectSettingsService {
    TermService termService

    def getList() {
        def startGrade = termService.minInSchoolGrade
        SubjectSettings.executeQuery '''
select new Map(
    subject.id as subjectId,
    subject.name as subjectName,
    department.id as departmentId,
    department.name as departmentName,
    director.id as directorId,
    director.name as directorName,
    secretary.id as secretaryId,
    secretary.name as secretaryName,
    (select max(m.grade) from Major m where m.subject = subject) as maxGrade
)
from Subject subject
join subject.department department
left join SubjectSettings ss with ss.subject.id = subject.id
left join Teacher director with ss.director.id = director.id
left join Teacher secretary with ss.secretary.id = secretary.id
where subject in (
  select m.subject
  from Major m
  where m.grade >= :startGrade
)
''', [startGrade: startGrade]
    }

    def update(SubjectSettingsCommand cmd) {
        SubjectSettings subjectSettings = SubjectSettings.get(cmd.subjectId)
        if (!subjectSettings) {
            subjectSettings = new SubjectSettings()
            subjectSettings.subject = Subject.load(cmd.subjectId)
        }

        if (cmd.directorId) {
            subjectSettings.director = Teacher.load(cmd.directorId)
        }

        if (cmd.secretaryId) {
            subjectSettings.secretary = Teacher.load(cmd.secretaryId)
        }

        subjectSettings.save()
    }
}
