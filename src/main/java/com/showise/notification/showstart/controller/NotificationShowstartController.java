package com.showise.notification.showstart.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;
import com.showise.message.model.MailService;
import com.showise.notification.showstart.model.NotificationShowstartService;
import com.showise.notification.showstart.model.NotificationShowstartVO;
import com.showise.session.model.SessionVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/notification_showstart")
public class NotificationShowstartController {

	@Autowired
	private NotificationShowstartService notificationShowstartSvc;

	@Autowired
	private MailService mailService;

	@Autowired
	private MemberService memberSvc;

	private String renderAdminLayout(Model model, String pageTitle, String contentFragment) {
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("content", contentFragment);
		return "back-end/layout/admin-layout";
	}

	@GetMapping("/update_notificationShowstart_input")
	public String updateNotificationShowstartInput(Model model) {

		NotificationShowstartVO vo = new NotificationShowstartVO();
		vo.setMember(new MemberVO());
		vo.setSession(new SessionVO());

		vo.setNotiShowstScon("親愛的用戶您好：\n此封訊息為提醒您電影開演的時間...");

		model.addAttribute("notificationShowstartVO", vo);

		// ✅ 下拉選單用（你頁面用 memberList 的話就保持 memberList）
		model.addAttribute("memberList", memberSvc.getAll());

		model.addAttribute("pageTitle", "即將開演通知編輯");
		model.addAttribute("content", "notification_showstart/update_notificationShowstart_input :: content");
		// return "back-end/layout/admin-layout";

		// model.addAttribute("notificationShowstartVO", new NotificationShowstartVO());

		return renderAdminLayout(model, "開演通知管理",
		"back-end/notification_showstart/update_notificationShowstart_input :: content");
	}

	@PostMapping("/insert")
	public String insert(@Valid NotificationShowstartVO notificationShowstartVO, BindingResult result,
			RedirectAttributes redirectAttributes, Model model) {

		if (result.hasErrors()) {
			model.addAttribute("notificationShowstartVO", notificationShowstartVO);
			return renderAdminLayout(model, "開演通知管理",
					"back-end/notification_showstart/update_notificationShowstart_input :: content"

			);
		}

		notificationShowstartSvc.addNotificationShowstart(notificationShowstartVO);
		redirectAttributes.addFlashAttribute("success", "- (新增成功)");
		return "redirect:/notification_showstart/listAllNotificationShowstart";
	}

	@PostMapping("/getOne_For_Update")
	public String getOne_For_Update(@RequestParam("notiShowstNo") Integer notiShowstNo, Model model) {

		NotificationShowstartVO vo = notificationShowstartSvc.getOneNotificationShowstart(notiShowstNo);

		model.addAttribute("notificationShowstartVO", vo);

		return renderAdminLayout(model, "開演通知管理",
				"back-end/notification_showstart/update_notificationShowstart_input :: content");
	}

	@PostMapping("/update")
	public String update(@Valid NotificationShowstartVO notificationShowstartVO, BindingResult result,
			@RequestParam(required = false) String mode,
			@RequestParam(name = "toEmail", required = false) String toEmail,
			@RequestParam(name = "notiShowstScon", required = false) String content, RedirectAttributes ra,
			Model model) {

		System.out.println("寄出成功");

		// ✅ 立即發送
		if ("sendNow".equals(mode)) {

			if (toEmail == null || toEmail.isBlank()) {
				ra.addFlashAttribute("error", "請先選擇會員信箱");
				return "redirect:/notification_showstart/update_notificationShowstart_input";
			}
			if (content == null || content.isBlank()) {
				ra.addFlashAttribute("error", "訊息內容不可空白");
				return "redirect:/notification_showstart/update_notificationShowstart_input";
			}

			try {
				mailService.sendTextMail(toEmail, "開演通知", content);
				ra.addFlashAttribute("success", "已寄出到：" + toEmail);
			} catch (Exception e) {
				e.printStackTrace();
				ra.addFlashAttribute("error", "寄信失敗：" + e.getMessage());
			}

			return "redirect:/notification_showstart/update_notificationShowstart_input";
		}

		//  一般修改 DB（原本的）
		if (result.hasErrors()) {
			model.addAttribute("notificationShowstartVO", notificationShowstartVO);
			return renderAdminLayout(model, "開演通知管理",
					"back-end/notification_showstart/update_notificationShowstart_input :: content");
		}

		notificationShowstartSvc.updateNotificationShowstart(notificationShowstartVO);
		ra.addFlashAttribute("success", "- (修改成功)");
		return "redirect:/notification_showstart/listAllNotificationShowstart";
	}

	@GetMapping("/listAllNotificationShowstart")
	public String listAll(Model model) {
		List<NotificationShowstartVO> list = notificationShowstartSvc.getAll();
		model.addAttribute("notificationShowstartVOListData", list);

		return renderAdminLayout(model, "開演通知管理",
				"back-end/notification_showstart/listAllNotificationShowstart :: content");
	}

	@GetMapping("/select_page")
	public String selectPage(Model model) {
		List<NotificationShowstartVO> list = notificationShowstartSvc.getAll();
		model.addAttribute("notificationShowstartVOListData", list);
//        model.addAttribute("notificationShowstartVOListData", Collections.emptyList());

		return renderAdminLayout(model, "開演通知管理", "back-end/notification_showstart/select_page :: content");
	}

	@GetMapping("/")
	public String home() {
		return "redirect:/";
	}

	@PostMapping("/listNotificationShowstarts_ByCompositeQuery")
	public String listNotificationShowstarts_ByCompositeQuery(HttpServletRequest req, Model model) {

		Map<String, String[]> parameterMap = req.getParameterMap();
		Map<String, String[]> criteria = new HashMap<>();

		String memberId = getFirstTrimmed(parameterMap, "memberId");
		String sessionId = getFirstTrimmed(parameterMap, "sessionId");
		String notiShowstStime = getFirstTrimmed(parameterMap, "notiShowstStime");

		java.util.function.Function<String, String> backToSelectPage = (String errorMsg) -> {
			model.addAttribute("errorMessage", errorMsg);
			model.addAttribute("notificationShowstartVOListData", Collections.emptyList());
			return renderAdminLayout(model, "開演通知管理", "back-end/notification_showstart/select_page :: content");
		};

		if (!memberId.isEmpty()) {
			if (!isDigits(memberId)) {
				return backToSelectPage.apply("會員編號必須為數字");
			}
			criteria.put("memberId", new String[] { memberId });
		}

		if (!sessionId.isEmpty()) {
			if (!isDigits(sessionId)) {
				return backToSelectPage.apply("場次編號必須為數字");
			}
			criteria.put("sessionId", new String[] { sessionId });
		}

		if (!notiShowstStime.isEmpty()) {
			criteria.put("notiShowstStime", new String[] { notiShowstStime });
		}

		List<NotificationShowstartVO> list = notificationShowstartSvc.getAll(criteria);

		model.addAttribute("notificationShowstartVOListData", list);

		return renderAdminLayout(model, "開演通知管理", "back-end/notification_showstart/select_page :: content");
	}

	private String getFirstTrimmed(Map<String, String[]> parameterMap, String key) {
		String[] values = parameterMap.get(key);
		if (values == null || values.length == 0 || values[0] == null) {
			return "";
		}
		return values[0].trim();
	}

	private boolean isDigits(String value) {
		return value != null && value.matches("\\d+");
	}

}
