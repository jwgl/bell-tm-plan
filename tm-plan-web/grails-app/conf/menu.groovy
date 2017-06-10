menuGroup 'main', {
    program 10, {
        settings         10, {
            subject      10, 'PERM_SUBJECT_SETUP',      '/web/plan/settings/subjects'
            program      20, 'PERM_PROGRAM_SETUP',      '/web/plan/settings/programs'
        }
        visionList       20, 'PERM_VISION_READ',        '/web/plan/visions'
        visionDraft      21, 'PERM_VISION_WRITE',       '/web/plan/users/${userId}/visions'
        visionCheck      22, 'PERM_VISION_CHECK',       '/web/plan/checkers/${userId}/visions'
        visionApproval   23, 'PERM_VISION_APPROVE',     '/web/plan/approvers/${userId}/visions'
        schemeList       30, 'PERM_SCHEME_READ',        '/web/plan/schemes'
        schemeDraft      31, 'PERM_SCHEME_WRITE',       '/web/plan/users/${userId}/schemes'
        schemeCheck      32, 'PERM_SCHEME_CHECK',       '/web/plan/checkers/${userId}/schemes'
        schemeApproval   33, 'PERM_SCHEME_APPROVE',     '/web/plan/approvers/${userId}/schemes'
        schemeAdmin      34, 'PERM_SCHEME_ADMIN',       '/web/plan/admin/schemes'
        schemeDeptAdmin  35, 'PERM_SCHEME_DEPT_ADMIN',  '/web/plan/departments/${departmentId}/schemes'
    }
}