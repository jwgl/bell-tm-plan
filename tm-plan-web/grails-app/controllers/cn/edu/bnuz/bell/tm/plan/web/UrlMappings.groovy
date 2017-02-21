package cn.edu.bnuz.bell.tm.plan.web

class UrlMappings {

    static mappings = {
        "/visions"(resources:'visionAdmin', includes: ['index'])

        "/schemes"(resources: 'schemeAdmin', includes: ['index'])

        "/reviewers"(resources: 'reviewer', includes: []) {
            "/visions"(resources:'visionReview', includes: []) {
                "/workitems"(resources: 'visionReview', includes: ['show'])
            }
            "/schemes"(resources: 'schemeReview', includes: []) {
                "/workitems"(resources: 'schemeReview', includes: ['show'])
            }
        }

        "/departments"(resources: 'department', includes: []) {
            "/visions"(resources: 'visionDepartment', includes: ['index'])
            "/schemes"(resources: 'schemeDepartment', includes: ['index'])
        }

        "/users"(resources: 'user', includes: []) {
            "/visions"(resources: 'visionDraft', includes: ['index'])
            "/schemes"(resources: 'schemeDraft', includes: ['index'])
        }

        group "/public", {
            "/visions"(resources:'visionPublic', includes: ['index', 'show'])
            "/schemes"(resources: 'schemePublic', includes: ['index', 'show'])
        }

        group "/settings", {
            "/subjects"(controller: "subjectSettings")
            "/programs"(controller: "programSettings")
        }

        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
