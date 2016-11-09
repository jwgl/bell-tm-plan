package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.Subject
import cn.edu.bnuz.bell.organization.Teacher

/**
 * 校内专业设置
 */
class SubjectSettings {
    /**
     * 虚拟ID，对应属性#subject
     */
    String id;

    /**
     * 校内专业
     */
    Subject subject

    /**
     * 专业负责人
     */
    Teacher director

    /**
     * 学院教务秘书
     */
    Teacher secretary

    static belongsTo = [subject: Subject]

    static mapping = {
        comment        '校内专业设置'
        id             column: 'subject_id', type: 'string', sqlType: 'varchar(4)', generator: 'foreign', params: [ property: 'subject']
        subject        comment: '校内专业', insertable: false, updateable: false
        director       comment: '专业负责人'
        secretary      comment: '学院教务秘书'
    }

    static constraints = {
        director       nullable: true
        secretary      nullable: true
    }
}
