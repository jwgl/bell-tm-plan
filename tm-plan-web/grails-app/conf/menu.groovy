menuGroup 'main', {
    program 10, {
        settings     10, {
            subject  10, 'PERM_SUBJECT_SETUP', '/web/plan/settings/subject'
            program  20, 'PERM_PROGRAM_SETUP', '/web/plan/settings/program'
        }
        visionList   20, 'PERM_VISION_READ',   '/web/plan/visions'
        visionDrafts 21, 'PERM_VISION_WRITE',  '/web/plan/users/${userId}/visions'
        schemeList   30, 'PERM_SCHEME_READ',   '/web/plan/schemes'
        schemeDrafts 31, 'PERM_SCHEME_WRITE',  '/web/plan/users/${userId}/schemes'
    }
}