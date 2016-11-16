package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.Period

class ProgramCourseDto implements Serializable {
    Integer programId
    String courseId
    Period period
    String propertyId
    Integer assessType
    Integer testType
    Integer startWeek
    Integer endWeek
    Integer suggestedTerm
    Integer allowedTerm
    String departmentId
    Integer directionId

    static embedded = ['period']

    static mapping = {
        table 'dv_program_course'
        id    composite: ['programId', 'courseId', 'directionId']
    }

    boolean equals(other) {
        if (!(other instanceof ProgramCourseDto)) {
            return false
        }

        other.programId == programId && other.courseId == courseId && other.directionId == directionId
    }
}
