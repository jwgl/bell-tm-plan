package cn.edu.bnuz.bell.tm.plan.api

class UrlMappings {

    static mappings = {
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

        "/departments"(resources: 'department', includes: ['index']) {
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
            "/grades"(controller: 'department', action: 'grades', method: 'GET')
        }

        "/checkers"(resources: 'checker', includes: []) {
            "/visions"(resources: 'visionCheck', includes:['index']) {
                "/workitems"(resources: 'visionCheck', includes: ['show', 'patch'])
                "/approvers"(controller: 'visionCheck', action: 'approvers', method: 'GET')
            }
            "/schemes"(resources: 'schemeCheck', includes:['index']) {
                "/workitems"(resources: 'schemeCheck', includes: ['show', 'patch'])
                "/approvers"(controller: 'schemeCheck', action: 'approvers', method: 'GET')
            }
        }

        "/approvers"(resources: 'approver', includes: []) {
            "/visions"(resources: 'visionApproval', includes:['index']) {
                "/workitems"(resources: 'visionApproval', includes: ['show', 'patch'])
            }
            "/schemes"(resources: 'schemeApproval', includes:['index']) {
                "/workitems"(resources: 'schemeApproval', includes: ['show', 'patch'])
            }
        }

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

        group "/admin", {
            "/visions"(resources: 'visionAdmin', includes: ['index', 'show'])
            "/schemes"(resources: 'schemeAdmin', includes: ['index', 'show'])
        }

        // 公共视图
        "/visions"(resources: 'visionPublic', includes: ['index', 'show'])
        "/schemes"(resources: 'schemePublic', includes: ['index', 'show']) {
            "/properties"(resources: 'property', includes: []) {
                "/courses"(controller: 'schemePublic', action: 'propertyCourses', method: 'GET')
            }
            "/directions"(resources: 'direction', includes: []) {
                "/courses"(controller: 'schemePublic', action: 'directionCourses', method: 'GET')
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
