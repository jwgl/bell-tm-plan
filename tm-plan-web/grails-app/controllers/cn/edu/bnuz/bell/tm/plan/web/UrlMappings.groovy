package cn.edu.bnuz.bell.tm.plan.web

class UrlMappings {

    static mappings = {
        "/visions"(resources:'visionPublic', includes: ['index', 'show']) {
            "/reviews"(resources: 'visionReview', includes:['show'])
        }

        "/schemes"(resources: 'schemePublic', includes: ['index', 'show']) {
            "/reviews"(resources: 'schemeReview', includes:['show'])
        }

        "/users"(resources: 'user') {
            "/visions"(resources: 'visionDraft', includes: ['index'])
            "/schemes"(resources: 'schemeDraft', includes: ['index'])
        }

        group "/settings", {
            "/subject"(controller: "subjectSetup")
            "/program"(controller: "programSetup")
        }

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
