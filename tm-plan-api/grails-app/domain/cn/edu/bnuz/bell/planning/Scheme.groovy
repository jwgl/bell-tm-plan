package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.workflow.StateObject
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.StateUserType
import cn.edu.bnuz.bell.workflow.WorkflowInstance

/**
 * 培养方案-教学安排
 * @author Yang Lin
 */
class Scheme implements StateObject {
    /**
     * 教学计划
     */
    Program program

    /**
     * 版本号
     * a.b.c.d <=> a << 24 & b << 16 & c << 8 & d
     * a : 0 - 255
     * b : 0 - 255
     * c : 0 - 255
     * d : 0 - 255
     */
    Integer versionNumber

    /**
     * 状态
     */
    State status

    /**
     * 上一版本
     */
    Scheme previous

    Set<SchemeCourse> courses

    WorkflowInstance workflowInstance

    static hasMany = [
        courses: SchemeCourse,
        tempCourses: SchemeTempCourse
    ]

    static mapping = {
        comment             '教学计划'
        dynamicUpdate       true
        id                  generator: 'identity', comment: '培养方案-教学安排ID'
        versionNumber       comment: '版本号'
        status              sqlType: 'state', type: StateUserType, comment: '状态'
        program             comment: '教学计划'
        previous            comment: '上一版本'
        workflowInstance    comment: '工作流实例'
        courses             cascade: 'all-delete-orphan'
        tempCourses         cascade: 'all-delete-orphan'
    }

    static constraints = {
        previous            nullable: true
        workflowInstance    nullable: true
    }

    String getWorkflowId() {
        this.previous ? 'scheme.revise' : 'scheme.create'
    }

    static Integer VERSION_INCREMENT = 1 << 8
    static Integer INITIAL_VERSION = 1 << 24
}
