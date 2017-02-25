package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 编辑培养方案
 * EndPoint: /users/${userId}/visions
 * @author Yang Lin
 */
@PreAuthorize('hasAuthority("PERM_VISION_WRITE")')
class VisionDraftController implements ServiceExceptionHandler{
    VisionDraftService visionDraftService
    VisionReviewerService visionReviewerService

    /**
     * 编辑列表
     * @param userId 当前用户ID
     * @return
     */
    def index(String userId) {
        renderJson visionDraftService.getVisions(userId)
    }

    /**
     * 显示时数据
     * @param userId 当前用户ID
     * @param id Vision ID
     * @return 显示数据
     */
    def show(String userId, Long id) {
        renderJson visionDraftService.getVisionForShow(id, userId)
    }

    /**
     * 创建时数据
     * @param userId 当前用户ID
     * @param type 类型: revise / 空（创建）
     * @return
     */
    def create(String userId, String type) {
        if (type == 'revise') {
            Long base = params.long('base')
            renderJson visionDraftService.getVisionForRevise(base, userId)
        } else {
            Integer programId = params.int('program')
            renderJson visionDraftService.getVisionForCreate(programId, userId)
        }
    }

    /**
     * 保存新建培养方案
     * @param userId 当前用户ID
     * @param type 新建类型：revise / 空（新建）
     * @return 新建培养方案ID
     */
    def save(String userId, String type) {
        if (type == 'revise') {
            def cmd = new VisionReviseCommand()
            bindData(cmd, request.JSON)
            def vision = visionDraftService.revise(cmd, userId)
            renderJson([id: vision.id])
        } else {
            def cmd = new VisionCreateCommand()
            bindData(cmd, request.JSON)
            def vision = visionDraftService.create(cmd, userId)
            renderJson([id: vision.id])
        }
    }

    /**
     * 编辑时数据
     * @param userId 当前用户ID
     * @param id Vision ID
     * @return 编辑数据
     */
    def edit(String userId, Long id) {
        renderJson visionDraftService.getVisionForEdit(id, userId)
    }

    /**
     * 更新
     * @param userId 当前用户ID
     * @param id Vision ID
     * @return 服务状态
     */
    def update(String userId, Long id) {
        def cmd = new VisionUpdateCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        visionDraftService.update(cmd, userId)
        renderOk()
    }

    /**
     * 删除
     * @param userId 当前用户ID
     * @param id Vision ID
     * @return 服务状态
     */
    def delete(String userId, Long id) {
        visionDraftService.delete(id, userId)
        renderOk()
    }

    /**
     * 提交
     * @param userId 当前用户ID
     * @param id 培养方案ID
     * @param op 操作
     * @return 服务状态
     */
    def patch(String userId, Long id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.SUBMIT:
                def cmd = new SubmitCommand()
                bindData(cmd, request.JSON)
                cmd.id = id
                visionDraftService.submit(cmd, userId)
                break
        }
        renderOk()
    }

    /**
     * 获取审核人
     * @param visionDraftId Vision ID
     * @return 审核人列表
     */
    def checkers(Long visionDraftId) {
        renderJson visionReviewerService.getCheckers(visionDraftId)
    }
}
