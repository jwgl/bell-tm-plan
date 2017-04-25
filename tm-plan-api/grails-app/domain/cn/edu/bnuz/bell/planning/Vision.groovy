package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.organization.Teacher
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
     * 创建人
     */
    Teacher creator

    /**
     * 创建时间
     */
    Date dateCreated

    /**
     * 修改时间
     */
    Date dateModified

    /**
     * 提交时间
     */
    Date dateSubmitted

    /**
     * 审核人
     */
    Teacher checker

    /**
     * 审核时间
     */
    Date dateChecked

    /**
     * 审批人
     */
    Teacher approver

    /**
     * 审批时间
     */
    Date dateApproved

    /**
     * 工作流实例
     */
    WorkflowInstance workflowInstance

    static mapping = {
        comment             '培养方案-目标与规格'
        dynamicUpdate       true
        id                  generator: 'identity', comment: '培养方案-目标与规格ID'
        program             type: 'integer', comment: '教学计划'
        versionNumber       unique: ['program'], comment: '版本号'
        status              sqlType: 'state', type: StateUserType, comment: '状态'
        objective           length: 2000, comment: '培养目标'
        specification       length: 2000, comment: '培养要求'
        schoolingLength     length: 2000, comment: '学制'
        awardedDegree       length: 1000, comment: '授予学位'
        previous            unique: ['program'], comment: '上一版本'
        creator             comment: '创建人'
        dateCreated         comment: '创建时间'
        dateModified        comment: '修改时间'
        dateSubmitted       comment: '提交时间'
        checker             comment: '审核人'
        dateChecked         comment: '审核时间'
        approver            comment: '审批人'
        dateApproved        comment: '审批时间'
        workflowInstance    comment: '工作流实例'
    }

    static constraints = {
        schoolingLength     nullable: true
        awardedDegree       nullable: true
        previous            nullable: true
        creator             nullable: true
        dateCreated         nullable: true
        dateModified        nullable: true
        dateSubmitted       nullable: true
        checker             nullable: true
        dateChecked         nullable: true
        approver            nullable: true
        dateApproved        nullable: true
        workflowInstance    nullable: true
    }

    String getWorkflowId() {
        this.previous ? REVISE_WORKFLOW_ID : CREATE_WORKFLOW_ID
    }

    static Integer VERSION_INCREMENT = 1 << 8
    static Integer INITIAL_VERSION = 1 << 24

    static String CREATE_WORKFLOW_ID = 'vision.create'
    static String REVISE_WORKFLOW_ID = 'vision.revise'
}