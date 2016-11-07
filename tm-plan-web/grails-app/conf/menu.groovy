menuGroup 'main', {
    program 10, {
        settings         10, {
            subject      10, 'PERM_SUBJECT_SETUP',      '/web/plan/settings/subject'
            program      20, 'PERM_PROGRAM_SETUP',      '/web/plan/settings/program'
        }
        visionList       20, 'PERM_VISION_READ',        '/web/plan/public/visions'
        visionDrafts     21, 'PERM_VISION_WRITE',       '/web/plan/users/${userId}/visions'
        schemeList       30, 'PERM_SCHEME_READ',        '/web/plan/public/schemes'
        schemeDrafts     31, 'PERM_SCHEME_WRITE',       '/web/plan/users/${userId}/schemes'
        schemeAdmin      32, 'PERM_SCHEME_ADMIN',       '/web/plan/schemes'
        schemeDeptAdmin  33, 'PERM_SCHEME_DEPT_ADMIN',  '/web/plan/departments/${departmentId}/schemes'
    }
}