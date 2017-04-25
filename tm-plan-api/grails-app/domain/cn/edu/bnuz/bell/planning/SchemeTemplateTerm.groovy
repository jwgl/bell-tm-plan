package cn.edu.bnuz.bell.planning

import org.codehaus.groovy.util.HashCodeHelper

/**
 * 培养方案模板-学期
 * @author Yang Lin
 */
class SchemeTemplateTerm implements Serializable {
    /**
     * 培养方案模板
     */
    SchemeTemplate schemeTemplate

    /**
     * 学期序号
     */
    Integer term


    /**
     * 显示顺序
     */
    Integer displayOrder

    static belongsTo = [schemeTemplate : SchemeTemplate]

    static mapping = {
        comment         '教学计划模板-学期'
        id              composite: ['schemeTemplate', 'term']
        schemeTemplate  comment: '培养方案模板'
        term            comment: '学期序号'
        displayOrder    comment: '显示顺序'
    }

    boolean equals(other) {
        if (!(other instanceof SchemeTemplateTerm)) {
            return false
        }

        other.schemeTemplate?.id == schemeTemplate?.id && other.term == term
    }

    int hashCode() {
        int hash = HashCodeHelper.initHash()
        hash = HashCodeHelper.updateHash(hash, schemeTemplate.id)
        hash = HashCodeHelper.updateHash(hash, term)
        hash
    }
}
