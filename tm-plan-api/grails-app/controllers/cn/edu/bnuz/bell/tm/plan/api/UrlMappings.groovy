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
            "/visions"(resources: 'visionDepartment', includes: ['index']) {
                collection {
                    "/latest"(controller: 'visionDepartment', action: 'latest', method: 'GET')
                }
            }
            "/schemes"(resources: 'schemeDepartment', includes: ['index', 'show']) {
                "/toes"(resources: 'schemeToes', includes: ['index', 'save'])
                collection {
                    "/latest"(controller: 'schemeDepartment', action: 'latest', method: 'GET')
                    "/directions"(controller: 'schemeDepartment', action: 'directions', method: 'GET')
                }
            }
        }

        // 培养方案管理
        "/visions"(resources: 'visionAdmin', includes: ['index', 'show'])

        // 教学计划管理
        "/schemes"(resources: 'schemeAdmin', includes: ['index', 'show'])

        // 审核
        "/reviewers"(resources: 'reviewer', includes: []) {
            "/visions"(resources:'visionReview', includes: []) {
                "/workitems"(resources: 'visionReview', includes: ['show', 'patch'])
                "/approvers"(controller: 'visionReview', action: 'approvers', method: 'GET')
            }
            "/schemes"(resources: 'schemeReview', includes: []) {
                "/workitems"(resources: 'schemeReview', includes: ['show', 'patch'])
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

        // 设置
        "/settings/subjects"(resources: 'subjectSettings', includes: ['index', 'update'])
        "/settings/programs"(resources: 'programSettings', includes: ['index', 'update']) {
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
