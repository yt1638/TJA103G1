package com.showise.notification.showstart.controller;

import com.showise.message.model.MessageService;
import com.showise.message.model.MessageVO;
import com.showise.notification.showstart.model.NotificationShowstartService;
import com.showise.notification.showstart.model.NotificationShowstartVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notification_showstart")
public class NotificationShowstartController {

    @Autowired
    private NotificationShowstartService service;
    
    @Autowired
    MessageService messageService;

    // 進入頁面 → List All
    @GetMapping("/listAll")
    public String listAll(Model model) {
        List<NotificationShowstartVO> list = service.getAll();
        model.addAttribute("notificationShowstartVOListData", list);
        model.addAttribute("pageTitle","通知管理");
        model.addAttribute("content","back-end/notification_showstart/select_page :: content");

        return "back-end/layout/admin-layout";
    }

    // 複合查詢
    @PostMapping("/listNotificationShowstarts_ByCompositeQuery")
    public String listByCompositeQuery(@RequestParam Map<String, String> paramMap,
                                       Model model) {

        List<NotificationShowstartVO> list = service.compositeQuery(paramMap);
        model.addAttribute("notificationShowstartVOListData", list);
        model.addAttribute("content","back-end/notification_showstart/select_page :: content");

        return "back-end/layout/admin-layout";
    }
    /* =========================
     * 進入編輯頁（開演通知 type = 0）
     * ========================= */
    @GetMapping("/update_notificationShowstart_input")
    public String editShowstartTemplate(Model model) {
        // type = 0 → 開演通知
        MessageVO messageVO = messageService.findByType(0);

        model.addAttribute("messageVO", messageVO); // 與 template th:object 統一
        model.addAttribute("pageTitle", "開演通知管理");
        model.addAttribute("content", "back-end/notification_showstart/update_notificationShowstart_input :: content");

        return "back-end/layout/admin-layout";
    }

    /* =========================
     * 儲存範本內容 + 提前小時
     * ========================= */
    @PostMapping("/update")
    public String updateShowstartTemplate(
            @RequestParam("msgContent") String msgContent,
            @RequestParam("preHours") Integer preHours,
            RedirectAttributes redirectAttributes) {

        try {
            // 取得開演通知範本
            MessageVO messageVO = messageService.findByType(0);

            // 更新內容與提前小時
            messageVO.setMsgContent(msgContent);
            messageVO.setPreHours(preHours);

            messageService.updateMessage(messageVO);

            redirectAttributes.addFlashAttribute("success", "開演通知範本已成功儲存");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "儲存失敗，請稍後再試");
        }

        return "redirect:/notification_showstart/update_notificationShowstart_input";
    }
}
    
    



