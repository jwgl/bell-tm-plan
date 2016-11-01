package cn.edu.bnuz.bell.tm.plan.api

class UrlMappings {

    static mappings = {
        // 按用户获取信息
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

        // 按学院获取信息
        "/departments"(resources: 'department', includes: []) {
            "/visions"(controller: 'visionPublic', action: 'indexByDepartment', method: 'GET')
            "/schemes"(controller: 'schemePublic', action: 'indexByDepartment', method: 'GET')
            "/directions"(controller: 'schemePublic', action: 'directionsByDepartment', method: 'GET')
        }

        // 培养方案
        "/visions"(resources: 'visionPublic', includes: ['index', 'show']) {
            "/reviews"(resources: 'visionReview', includes: ['show', 'patch']) {
                "/approvers"(controller: 'visionReview', action: 'approvers', method: 'GET')
            }
        }

        // 教学计划
        "/schemes"(resources: 'schemePublic', includes: ['index', 'show']) {
            "/reviews"(resources: 'schemeReview', includes: ['show', 'patch']) {
                "/approvers"(controller: 'schemeReview', action: 'approvers', method: 'GET')
            }
            "/properties"(resources: 'property', includes: []) {
                "/courses"(controller: 'schemePublic', action: 'propertyCourses', method: 'GET')
            }
            "/directions"(resources: 'direction', includes: []) {
                "/courses"(controller: 'schemePublic', action: 'directionCourses', method: 'GET')
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
