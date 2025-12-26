package com.showise.tickettype.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import com.showise.tickettype.model.TicketTypeService;
import com.showise.tickettype.model.TicketTypeVO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/back_end/ticket/ticket.do")

/**
 * 處理 ticket 相關請求的 Servlet
 * 需要搭配 JSP 的 form 設定：enctype="multipart/form-data"
 */
@MultipartConfig(
        fileSizeThreshold = 1 * 1024 * 1024,    // 1MB
        maxFileSize = 20 * 1024 * 1024,         // 單一檔案最大 20MB
        maxRequestSize = 40 * 1024 * 1024       // 整體 request 最大 40MB
)
public class TicketTypeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doPost(req, res);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        System.out.println(action);

        /***************** getOne_For_Display：單筆查詢 *****************/
        if ("getOne_For_Display".equals(action)) {

            List<String> errorMsgs = new LinkedList<>();
            req.setAttribute("errorMsgs", errorMsgs);

            // 1. 同時抓三種可能的查詢條件
            String idStr    = req.getParameter("ticketID");      // 輸入的編號
            String nameStr  = req.getParameter("ticketName");    // 下拉選名稱
            String priceStr = req.getParameter("ticketPrice");   // 下拉選價格
            

            // 三個都沒填就錯誤
            if ((idStr == null || idStr.trim().isEmpty()) &&
                (nameStr == null || nameStr.trim().isEmpty()) &&
                (priceStr == null || priceStr.trim().isEmpty())) {

                errorMsgs.add("請輸入或選擇一種查詢條件");
                RequestDispatcher failureView = req
                        .getRequestDispatcher("/back_end/ticket/select_page.jsp");
                failureView.forward(req, res);
                return;
            }

            TicketTypeService ticketSvc = new TicketTypeService();
            TicketTypeVO ticketVO = null;

            try {
              
                List<TicketTypeVO> list = ticketSvc.getAll();

                // 1) 有輸入編號 → 用編號找
                if (idStr != null && !idStr.trim().isEmpty()) {
                    Integer ticketID = null;
                    try {
                        ticketID = Integer.valueOf(idStr.trim());
                    } catch (NumberFormatException e) {
                        errorMsgs.add("票種編號格式不正確");
                    }

                    if (!errorMsgs.isEmpty()) {
                        RequestDispatcher failureView = req
                                .getRequestDispatcher("/back_end/ticket/select_page.jsp");
                        failureView.forward(req, res);
                        return;
                    }

                    for (TicketTypeVO vo : list) {
                        if (vo.getTicketID() != null &&
                            vo.getTicketID().intValue() == ticketID.intValue()) {
                            ticketVO = vo;
                            break;
                        }
                    }
                }
                // 2) 沒輸入編號，有選名稱 → 用名稱找
                else if (nameStr != null && !nameStr.trim().isEmpty()) {
                    for (TicketTypeVO vo : list) {
                        if (nameStr.equals(vo.getTicketName())) {
                            ticketVO = vo;
                            break;
                        }
                    }
                }
                // 3) 只有選價格 → 用價格找
                else if (priceStr != null && !priceStr.trim().isEmpty()) {
                    Integer price = null;
                    try {
                        price = Integer.valueOf(priceStr.trim());
                    } catch (NumberFormatException e) {
                        errorMsgs.add("票價格式不正確");
                    }

                    if (!errorMsgs.isEmpty()) {
                        RequestDispatcher failureView = req
                                .getRequestDispatcher("/back_end/ticket/select_page.jsp");
                        failureView.forward(req, res);
                        return;
                    }

                    for (TicketTypeVO vo : list) {
                        if (vo.getTicketPrice() != null &&
                            vo.getTicketPrice().intValue() == price.intValue()) {
                            ticketVO = vo;
                            break;
                        }
                    }
                }

                // 找不到資料
                if (ticketVO == null) {
                    errorMsgs.add("查無資料");
                }

                if (!errorMsgs.isEmpty()) {
                    RequestDispatcher failureView = req
                            .getRequestDispatcher("/back_end/ticket/select_page.jsp");
                    failureView.forward(req, res);
                    return;
                }

                // 3. 查詢完成，轉交到單筆顯示
                req.setAttribute("ticketVO", ticketVO);
                String url = "/back_end/ticket/listOneTicket.jsp";
                RequestDispatcher successView = req.getRequestDispatcher(url);
                successView.forward(req, res);

            } catch (Exception e) {
                errorMsgs.add("查詢過程發生錯誤：" + e.getMessage());
                RequestDispatcher failureView = req
                        .getRequestDispatcher("/back_end/ticket/select_page.jsp");
                failureView.forward(req, res);
            }
        }


        /***************** getOne_For_Update：進入修改頁 *****************/
        else if ("getOne_For_Update".equals(action)) { // 來自 listAllTicket.jsp 的請求

            List<String> errorMsgs = new LinkedList<>();
            req.setAttribute("errorMsgs", errorMsgs);

            // 1. 接收參數
            Integer ticketID = Integer.valueOf(req.getParameter("ticketID"));
            

            // 2. 查詢資料
            TicketTypeService ticketSvc = new TicketTypeService();
            TicketTypeVO ticketVO = ticketSvc.getOneTicket(ticketID);

            // 3. 查詢完成，轉交到更新頁
            req.setAttribute("ticketVO", ticketVO);
            String url = "/back_end/ticket/update_ticket_input.jsp";
            RequestDispatcher successView = req.getRequestDispatcher(url);
            successView.forward(req, res);
        }
        if ("getOne_For_Update".equals(action)) {

            // 1. 取得參數
            String ticketIDStr = req.getParameter("ticketID"); // 對應 listAllTicket.jsp 的 hidden
            Integer ticketID = Integer.valueOf(ticketIDStr);

            // 2. 查詢資料
            TicketTypeService ticketSvc = new TicketTypeService();
            TicketTypeVO ticketVO = ticketSvc.getOneTicket(ticketID);

            // 3. 存入 request scope
            req.setAttribute("ticketVO", ticketVO);

            // 4. 轉交到修改頁面
            String url = "/back_end/ticket/update_ticket_input.jsp";
            RequestDispatcher successView = req.getRequestDispatcher(url);
            successView.forward(req, res);
            return;
        }

        /***************** update：修改資料 *****************/
        else if ("update".equals(action)) { // 來自 update_ticket_input.jsp 的請求

            List<String> errorMsgs = new LinkedList<>();
            req.setAttribute("errorMsgs", errorMsgs);

            // 先準備好一個 VO，錯誤時可以帶回畫面
            TicketTypeVO ticketVO = new TicketTypeVO();

            try {
                /***** 1. 接收請求參數並做格式檢查 *****/
                Integer ticketID = Integer.valueOf(req.getParameter("ticketID").trim());
                ticketVO.setTicketID(ticketID);

                // 票種名稱
                String ticketName = req.getParameter("ticketName");
                String ticketNameReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$";
                if (ticketName == null || ticketName.trim().length() == 0) {
                    errorMsgs.add("名稱請勿空白");
                } else if (!ticketName.trim().matches(ticketNameReg)) {
                    errorMsgs.add("名稱只能是中、英文字母、數字和_，且長度必需在2到10之間");
                }
                ticketVO.setTicketName(ticketName);

                // 價格
                Integer ticketPrice = null;
                String priceStr = req.getParameter("ticketPrice");
                if (priceStr == null || priceStr.trim().length() == 0) {
                    errorMsgs.add("票價請勿空白");
                } else {
                    try {
                        ticketPrice = Integer.valueOf(priceStr.trim());
                        if (ticketPrice < 0) {
                            errorMsgs.add("票價請輸入正整數");
                        }
                    } catch (NumberFormatException e) {
                        errorMsgs.add("票價請輸入數字");
                    }
                }
                ticketVO.setTicketPrice(ticketPrice);
                // 內容描述
                String ticketDescription = req.getParameter("ticketDescription");
                String ticketDescriptionReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,50}$";
                if (ticketDescription == null || ticketDescription.trim().length() == 0) {
                    errorMsgs.add("內容請勿空白");
                } else if (!ticketDescription.trim().matches(ticketDescriptionReg)) {
                    errorMsgs.add("內容只能是中、英文字母、數字和_，且長度必需在2到50之間");
                }
                ticketVO.setTicketDescription(ticketDescription);

             // 圖片：有上傳就用新圖；沒上傳就沿用 DB 舊圖
                Part part = req.getPart("ticketImage");
                byte[] ticketImage = null;

                if (part != null && part.getSize() > 0) {
                    try (InputStream in = part.getInputStream()) {
                        ticketImage = in.readAllBytes();
                    }
                } else {
                    // 沿用原本資料庫的圖片
                    TicketTypeService ticketSvcTmp = new TicketTypeService();
                    TicketTypeVO oldVO = ticketSvcTmp.getOneTicket(ticketID);
                    ticketImage = (oldVO != null) ? oldVO.getTicketImage() : null;
                }
                ticketVO.setTicketImage(ticketImage);


                // 若有錯誤，退回輸入頁
                if (!errorMsgs.isEmpty()) {
                    req.setAttribute("ticketVO", ticketVO); // 含有錯誤的資料，回傳給 JSP 顯示
                    RequestDispatcher failureView = req
                            .getRequestDispatcher("/back_end/ticket/update_ticket_input.jsp");
                    failureView.forward(req, res);
                    return;
                }

                /***** 2. 開始修改資料 *****/
                TicketTypeService ticketSvc = new TicketTypeService();
                ticketVO = ticketSvc.updateTicket(ticketID, ticketName, ticketPrice,
                        ticketDescription, ticketImage);

                /***** 3. 修改完成，轉交顯示單筆 *****/
                req.setAttribute("ticketVO", ticketVO); // 正確的 ticketVO，存入 req
                String url = "/back_end/ticket/listOneTicket.jsp";
                RequestDispatcher successView = req.getRequestDispatcher(url);
                successView.forward(req, res);

            } catch (Exception e) {
                errorMsgs.add("修改資料失敗：" + e.getMessage());
                req.setAttribute("ticketVO", ticketVO);
                RequestDispatcher failureView = req
                        .getRequestDispatcher("/back_end/ticket/update_ticket_input.jsp");
                failureView.forward(req, res);
            }
        }

        /***************** insert：新增資料 *****************/
        else if ("insert".equals(action)) { // 來自 addTicket.jsp 的請求

            List<String> errorMsgs = new LinkedList<>();
            req.setAttribute("errorMsgs", errorMsgs);

            TicketTypeVO ticketVO = new TicketTypeVO();

            try {
                /***** 1. 接收請求參數並做格式檢查 *****/
                // 票種名稱
                String ticketName = req.getParameter("ticketName");
                String ticketNameReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$";
                if (ticketName == null || ticketName.trim().length() == 0) {
                    errorMsgs.add("名稱請勿空白");
                } else if (!ticketName.trim().matches(ticketNameReg)) {
                    errorMsgs.add("名稱只能是中、英文字母、數字和_，且長度需在2到10之間");
                }
                ticketVO.setTicketName(ticketName);

                // 票價
                Integer ticketPrice = null;
                try {
                    ticketPrice = Integer.valueOf(req.getParameter("ticketPrice").trim());
                    if (ticketPrice < 0) {
                        errorMsgs.add("票價請輸入正整數");
                    }
                } catch (NumberFormatException e) {
                    errorMsgs.add("票價請輸入數字");
                }
                ticketVO.setTicketPrice(ticketPrice);

                // 內容描述
                String ticketDescription = req.getParameter("ticketDescription");
                String ticketDescriptionReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,50}$";
                if (ticketDescription == null || ticketDescription.trim().length() == 0) {
                    errorMsgs.add("內容請勿空白");
                } else if (!ticketDescription.trim().matches(ticketDescriptionReg)) {
                    errorMsgs.add("內容只能是中、英文字母、數字和_，且長度需在2到50之間");
                }
                ticketVO.setTicketDescription(ticketDescription);

                // 圖片
                Part part = req.getPart("ticketImage"); // 對應 JSP 的 name="ticketImage"
                byte[] ticketImage = null;
                if (part != null && part.getSize() > 0) {
                    try (InputStream in = part.getInputStream()) {
                        ticketImage = in.readAllBytes();
                    }
                } else {
                    errorMsgs.add("請上傳圖片");
                }
                ticketVO.setTicketImage(ticketImage);

                // 如果有錯誤，回到 addTicket.jsp
                if (!errorMsgs.isEmpty()) {
                    req.setAttribute("ticketVO", ticketVO);
                    RequestDispatcher failureView = req.getRequestDispatcher("/back_end/ticket/addTicket.jsp");
                    failureView.forward(req, res);
                    return;
                }

                /***** 2. 開始新增資料 *****/
                TicketTypeService ticketSvc = new TicketTypeService();
                ticketVO = ticketSvc.addTicket(ticketName, ticketPrice, ticketDescription, ticketImage);

                /***** 3. 新增完成，轉交回 listAllTicket.jsp *****/
                req.setAttribute("ticketVO", ticketVO);
                String url = "/back_end/ticket/listAllTicket.jsp";
                RequestDispatcher successView = req.getRequestDispatcher(url);
                successView.forward(req, res);

            } catch (Exception e) {
                errorMsgs.add("新增資料失敗：" + e.getMessage());
                req.setAttribute("ticketVO", ticketVO);
                RequestDispatcher failureView = req
                        .getRequestDispatcher("/back_end/ticket/addTicket.jsp");
                failureView.forward(req, res);
            }
        }


        /***************** delete：刪除資料 *****************/
        else if ("delete".equals(action)) { // 來自 listAllTicket.jsp

            List<String> errorMsgs = new LinkedList<>();
            req.setAttribute("errorMsgs", errorMsgs);

            try {
                // 1. 接收參數
                Integer ticketID = Integer.valueOf(req.getParameter("ticketID"));

                // 2. 刪除資料
                TicketTypeService ticketSvc = new TicketTypeService();
                ticketSvc.deleteTicket(ticketID);

                // 3. 刪除完成，回到列表
                String url = "/back_end/ticket/listAllTicket.jsp";
                RequestDispatcher successView = req.getRequestDispatcher(url);
                successView.forward(req, res);

            } catch (Exception e) {
                errorMsgs.add("刪除資料失敗：" + e.getMessage());
                String url = "/back_end/ticket/listAllTicket.jsp";
                RequestDispatcher failureView = req.getRequestDispatcher(url);
                failureView.forward(req, res);
            }
        }
    }
}
