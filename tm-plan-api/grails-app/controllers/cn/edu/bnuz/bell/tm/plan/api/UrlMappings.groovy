package cn.edu.bnuz.bell.tm.plan.api

class UrlMappings {

    static mappings = {
        // 编辑
        "/users"(resources: 'user', includes: []) {
            "/visions"(resources: 'visionDraft') {
                "/checkers"(controller: 'visionDraft', action: 'checkers', method: 'GET')
            }
            "/schemes"(resources: 'schemeDraft') {
                "/checkers"(controller: 'schemeDraft', action: 'checkers', method: 'GET')
                collection {
                    "/courses"(controller: 'schemeDraft', action: 'courses', method: 'GET')
                }
            }
        }

        // 学院管理
        "/departments"(resources: 'department', includes: []) {
            "/visions"(controller: 'visionDepartment', action: 'index', method: 'GET')
            "/schemes"(controller: 'schemeDepartment', action: 'index', method: 'GET')
            "/directions"(controller: 'schemeDepartment', action: 'directions', method: 'GET')
        }

        // 培养方案管理
        "/visions"(resources: 'visionAdmin', includes: ['index', 'show']) {
            "/reviews"(resources: 'visionReview', includes: ['show', 'patch']) {
                "/approvers"(controller: 'visionReview', action: 'approvers', method: 'GET')
            }
        }

        // 教学计划管理
        "/schemes"(resources: 'schemeAdmin', includes: ['index', 'show']) {
            "/reviews"(resources: 'schemeReview', includes: ['show', 'patch']) {
                "/approvers"(controller: 'schemeReview', action: 'approvers', method: 'GET')
            }
        }

        // 公共视图
        group "/public", {
            "/visions"(resources: 'visionPublic', includes: ['index', 'show'])
            "/schemes"(resources: 'schemePublic', includes: ['index', 'show']) {
                "/properties"(resources: 'property', includes: []) {
                    "/courses"(controller: 'schemePublic', action: 'propertyCourses', method: 'GET')
                }
                "/directions"(resources: 'direction', includes: []) {
                    "/courses"(controller: 'schemePublic', action: 'directionCourses', method: 'GET')
                }
            }
        }

        // 教学计划模板
        "/schemeTemplates"(resources: 'schemeTemplate', includes: ['index'])

        // 专业负责人
        "/subjectDirectors"(resources: 'subjectDirector', includes: ['index', 'save'])

        // 计划设置
        "/programSettings"(resources: 'programSettings', includes: ['index', 'update']) {
            collection {
                "/grades"(controller: 'programSettings', action: 'grades', method: 'GET')
            }
        }

        "500"(view: '/error')
        "404"(view: '/notFound')
        "403"(view: '/forbidden')
        "401"(view: '/unauthorized')
    }
}
