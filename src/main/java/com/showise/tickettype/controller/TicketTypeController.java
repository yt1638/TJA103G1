package com.showise.tickettype.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.showise.tickettype.model.TicketTypeService;
import com.showise.tickettype.model.TicketTypeVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tickettype")
public class TicketTypeController {

    @Autowired
    private TicketTypeService ticketTypeSvc;

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
        model.addAttribute(
            "content",
            "back-end/tickettype/select_page :: content"
        );

        return "back-end/layout/admin-layout";
    }

    @PostMapping("/update")
    public String update(@Valid TicketTypeVO ticketTypeVO,
                         BindingResult result,
                         Model model) {

        if (result.hasErrors()) {
            prepareTicketEditModel(model);

            model.addAttribute("pageTitle", "票券編輯");
            model.addAttribute(
                "content",
                "back-end/tickettype/select_page :: content"
            );

            return "back-end/layout/admin-layout";
        }

        ticketTypeSvc.updateTicket(ticketTypeVO);

        return "redirect:/tickettype/select_page";
    }
    @Autowired
    private TicketTypeService ticketTypeService;

    @PostMapping("/tickettype/update")
    public String updateTicketType(TicketTypeVO ticketTypeVO) {

        ticketTypeService.update(ticketTypeVO);

        return "redirect:/tickettype/edit";  
    }
}

