package cn.edu.bnuz.bell.planning

class ProgramCoursesCommand {
    List<ProgramCourse> courses

    class ProgramCourse {
        Long schemeCourseId
        Integer startWeek
        Integer endWeek
        Integer testType
        String departmentId
    }
}
