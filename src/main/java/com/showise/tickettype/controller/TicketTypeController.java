package com.showise.tickettype.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.showise.tickettype.model.TicketTypeService;
import com.showise.tickettype.model.TicketTypeVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tickettype")
public class TicketTypeController {

    @Autowired
    private TicketTypeService ticketTypeSvc;

    // 讓 select_page 需要的 model 都準備好（全票 + 優待票）
    private void prepareTicketEditModel(Model model) {
        List<TicketTypeVO> list = ticketTypeSvc.getAll();
        model.addAttribute("ticketTypeListData", list);

        TicketTypeVO full = list.stream()
                .filter(t -> "全票".equals(t.getTicketName()))
                .findFirst()
                .orElseGet(() -> {
                    TicketTypeVO vo = new TicketTypeVO();
                    vo.setTicketName("全票");
                    return vo;
                });

        TicketTypeVO discount = list.stream()
                .filter(t -> "優待票".equals(t.getTicketName()))
                .findFirst()
                .orElseGet(() -> {
                    TicketTypeVO vo = new TicketTypeVO();
                    vo.setTicketName("優待票");
                    return vo;
                });

        model.addAttribute("fullTicket", full);
        model.addAttribute("discountTicket", discount);
    }

    @GetMapping("/select_page")
    public String selectPage(Model model) {

        prepareTicketEditModel(model);

        model.addAttribute("pageTitle", "票券編輯");
        model.addAttribute("content", "back-end/tickettype/select_page :: content");

        return "back-end/layout/admin-layout";
    }

    @PostMapping("/update")
    public String update(@Valid TicketTypeVO ticketTypeVO,
                         BindingResult result,
                         Model model) {

        // 先跑 VO 自己的欄位驗證（例如 @NotNull / @Min）
        if (result.hasErrors()) {
            prepareTicketEditModel(model);
            model.addAttribute("pageTitle", "票券編輯");
            model.addAttribute("content", "back-end/tickettype/select_page :: content");
            return "back-end/layout/admin-layout";
        }

        // ✅ 跨欄位規則：如果更新的是「優待票」，就檢查它必須小於「全票」
        if ("優待票".equals(ticketTypeVO.getTicketName())) {

            // 找出 DB 目前的全票
            Optional<TicketTypeVO> fullOpt = ticketTypeSvc.getAll().stream()
                    .filter(t -> "全票".equals(t.getTicketName()))
                    .findFirst();

            if (fullOpt.isPresent()) {
                TicketTypeVO full = fullOpt.get();

                BigDecimal discountPrice = toBigDecimal(ticketTypeVO.getTicketPrice());
                BigDecimal fullPrice = toBigDecimal(full.getTicketPrice());

                // 兩者都有值才比較（避免 NullPointer）
                if (discountPrice != null && fullPrice != null) {
                    // discount 必須 < full
                    if (discountPrice.compareTo(fullPrice) >= 0) {
                        // 把錯誤綁到 ticketPrice 欄位（你的表單要能顯示 th:errors="*{ticketPrice}"）
                    	result.rejectValue(
                    		    "ticketPrice",
                    		    "discount.less.than.full",
                    		    "⚠ 優待票金額不可超過全票金額"
                    		);

                        prepareTicketEditModel(model);
                        model.addAttribute("pageTitle", "票券編輯");
                        model.addAttribute("content", "back-end/tickettype/select_page :: content");
                        return "back-end/layout/admin-layout";
                    }
                }
            }
        }

        // 真的通過才更新
        ticketTypeSvc.updateTicket(ticketTypeVO);

        return "redirect:/tickettype/select_page";
    }

    /**
     * 把各種數字型別（Integer/Long/BigDecimal...）安全轉成 BigDecimal 做比較
     * - 若 price 為 null 直接回 null
     */
    private BigDecimal toBigDecimal(Object price) {
        if (price == null) return null;

        if (price instanceof BigDecimal) {
            return (BigDecimal) price;
        }
        if (price instanceof Integer) {
            return BigDecimal.valueOf((Integer) price);
        }
        if (price instanceof Long) {
            return BigDecimal.valueOf((Long) price);
        }
        if (price instanceof Double) {
            return BigDecimal.valueOf((Double) price);
        }
        if (price instanceof Float) {
            return BigDecimal.valueOf(((Float) price).doubleValue());
        }
        if (price instanceof Number) {
            return BigDecimal.valueOf(((Number) price).doubleValue());
        }

        // 的 price 是 String
        try {
            return new BigDecimal(price.toString().trim());
        } catch (Exception e) {
            return null;
        }      
    }
    
}
