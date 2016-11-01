package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.workflow.StateObject
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.StateUserType
import cn.edu.bnuz.bell.workflow.WorkflowInstance

/**
 * 培养方案-目标与规格
 * @author Yang Lin
 */
class Vision implements StateObject {
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
     * 状态-0：新建；1-待审核；2-待审批；3：不通过；4：通过
     */
    State status

    /**
     * 培养目标
     */
    String objective

    /**
     * 业务规格
     */
    String specification

    /**
     * 学制
     */
    String schoolingLength

    /**
     * 授予学位
     */
    String awardedDegree

    /**
     * 上一版本
     */
    Vision previous

    /**
     * 工作流实例
     */
    WorkflowInstance workflowInstance

    static mapping = {
        comment             '培养方案-目标与规格'
        dynamicUpdate       true
        id                  generator: 'identity', comment: '培养方案-目标与规格ID'
        program             type: 'integer', comment: '教学计划'
        versionNumber       comment: '版本号'
        status              sqlType: 'state', type: StateUserType, comment: '状态'
        objective           length: 2000, comment: '培养目标'
        specification       length: 2000, comment: '培养要求'
        schoolingLength     length: 2000, comment: '学制'
        awardedDegree       length: 1000, comment: '授予学位'
        previous            comment: '上一版本'
        workflowInstance    comment: '工作流实例'
    }

    static constraints = {
        schoolingLength      nullable: true
        awardedDegree        nullable: true
        previous             nullable: true
        workflowInstance     nullable: true
    }

    String getWorkflowId() {
        this.previous ? 'vision.revise' : 'vision.create'
    }

    static Integer VERSION_INCREMENT = 1 << 8
    static Integer INITIAL_VERSION = 1 << 24
}