package cn.edu.bnuz.bell.planning

/**
 * 计划课程数据传输对象
 * @author Yang Lin
 */
class SchemeCourseDto {
    String id
    String name
    BigDecimal credit
    BigDecimal theoryPeriod
    BigDecimal experimentPeriod
    Integer periodWeeks
    Boolean isPractical
    Integer educationLevel
    Integer assessType
    Integer propertyId
    String departmentId
    String departmentName
    Boolean isTempCourse

    static mapping = {
        table 'dv_scheme_course'
    }
}
