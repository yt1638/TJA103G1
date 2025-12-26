package com.showise.tickettype.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

   
       

    /** 查一筆進入修改頁 */
    @PostMapping("/getOne_For_Update")
    public String getOne_For_Update(@RequestParam("ticketTypeId") Integer ticketTypeId,
                                    ModelMap model) {

    	TicketTypeVO ticketTypeVO =
    			ticketTypeSvc.getOneById(ticketTypeId);

        model.addAttribute("ticketTypeVO", ticketTypeVO);
        return "back-end/tickettype/update_TicketType_input";
    }

    /** 修改 */
    @PostMapping("/update")
    public String update(@Valid TicketTypeVO ticketTypeVO,
                         BindingResult result,
                         ModelMap model) {

        if (result.hasErrors()) {
            return "back-end/tickettype/update_TicketType_input";
        }

        ticketTypeSvc.updateTicket(ticketTypeVO);


        return "redirect:/tickettype/listAllTicketType";
    }

    

    /** 列出全部 */
    @GetMapping("/")
    public String listAll(ModelMap model) {
        List<TicketTypeVO> list = ticketTypeSvc.getAll();
        model.addAttribute("tiketTypeData", list);
        return "back-end/tickettype/listAllTicketType";
    }



}

