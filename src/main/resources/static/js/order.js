// /js/order.js
document.addEventListener("DOMContentLoaded", () => {
  // ===== DOM =====
  const movieSelect = document.querySelector("#movie-select");
  const dateSelect = document.querySelector("#date-select");

  const summaryMovie = document.querySelector("#summary-movie");
  const summaryDate = document.querySelector("#summary-date");
  const summaryTime = document.querySelector("#summary-time");
  const summaryTicketTbody = document.querySelector("#summary-ticket-table tbody");
  const summaryFoodTbody = document.querySelector("#summary-food-table tbody");
  const summaryTotal = document.querySelector("#summary-total");

  // ===== utils =====
  const money = (n) => "$" + Number(n || 0).toLocaleString("zh-TW");
  const esc = (s) =>
    String(s ?? "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#39;");
	  
  // ===== 必須至少 1 張票：擋住 prepareSeatForm submit（UX）=====
//  const prepareSeatForm = document.querySelector("#prepareSeatForm");
//  prepareSeatForm?.addEventListener("submit", (e) => {
//    const totalQty = Array.from(document.querySelectorAll("input.ticket-qty"))
//      .reduce((sum, inp) => sum + Math.max(0, Number(inp.value || 0)), 0);
//
//    if (totalQty <= 0) {
//      e.preventDefault();
//      alert("請至少選 1 張票");
//    }
//  });
	  

  function getSelectedMovieName() {
    const opt = movieSelect?.selectedOptions?.[0];
    return opt && opt.value ? opt.textContent.trim() : "—";
  }

  function getSelectedDate() {
    return dateSelect?.value ? dateSelect.value : "—";
  }

  function getSelectedTimeText() {
    // 你按場次會 POST + redirect，回來後 Thymeleaf 會加上 active selected
    const btn = document.querySelector(".time-button.active, .time-button.selected");
    return btn ? btn.textContent.trim() : "—";
  }

  //票種讀取:
  function readTickets() {
    const items = [];
    let total = 0;

    document.querySelectorAll("input.ticket-qty").forEach((inp) => {
      const qty = Math.max(0, Number(inp.value || 0));
      inp.value = String(qty);

      const name = inp.dataset.ticketName || "票種";
      const price = Number(inp.dataset.price || 0);
      const subtotal = qty * price;

      // 左側小計格（若存在）
      const id = inp.dataset.ticketTypeId;
      const cell = document.querySelector(`.ticket-subtotal[data-ticket-type-id="${id}"]`);
      if (cell) cell.textContent = money(subtotal);

      if (qty > 0) {
        items.push({ name, qty, subtotal });
        total += subtotal;
      }
    });

    return { items, total };
  }

  function readFoods() {
    const items = [];
    let total = 0;

    document.querySelectorAll(".product-card").forEach((card) => {
      const name = card.dataset.name || "餐飲";
      const price = Number(card.dataset.price || 0);
      const qtyInput = card.querySelector("input.food-qty");
      if (!qtyInput) return;

      const qty = Math.max(0, Number(qtyInput.value || 0));
      qtyInput.value = String(qty);

      if (qty > 0) {
        const subtotal = qty * price;
        items.push({ name, qty, subtotal });
        total += subtotal;
      }
    });

    return { items, total };
  }

  function renderSummary() {
    // 1) movie/date/time
    if (summaryMovie) summaryMovie.textContent = getSelectedMovieName();
    if (summaryDate) summaryDate.textContent = getSelectedDate();
    if (summaryTime) summaryTime.textContent = getSelectedTimeText();

    // 2) tickets
    const t = readTickets();
    if (summaryTicketTbody) {
      summaryTicketTbody.innerHTML = "";
      if (t.items.length === 0) {
        summaryTicketTbody.innerHTML = `<tr><td colspan="3">未選擇票種</td></tr>`;
      } else {
        t.items.forEach((it) => {
          summaryTicketTbody.insertAdjacentHTML(
            "beforeend",
            `<tr>
              <td>${esc(it.name)}</td>
              <td>${it.qty}</td>
              <td>${money(it.subtotal)}</td>
            </tr>`
          );
        });
      }
    }

    // 3) foods
    const f = readFoods();
    if (summaryFoodTbody) {
      summaryFoodTbody.innerHTML = "";
      if (f.items.length === 0) {
        summaryFoodTbody.innerHTML = `<tr><td colspan="3">未加購餐飲</td></tr>`;
      } else {
        f.items.forEach((it) => {
          summaryFoodTbody.insertAdjacentHTML(
            "beforeend",
            `<tr>
              <td>${esc(it.name)}</td>
              <td>${it.qty}</td>
              <td>${money(it.subtotal)}</td>
            </tr>`
          );
        });
      }
    }

    // 4) total
    const grand = t.total + f.total;
    if (summaryTotal) summaryTotal.textContent = money(grand);
  }

  // ===== bind: qty input => re-render =====
  document.querySelectorAll("input.ticket-qty, input.food-qty").forEach((inp) => {
    inp.addEventListener("input", renderSummary);
  });

  // tabs 切換後也要更新（因為有些food-qty在不同tab）
  document.querySelectorAll(".tab-button").forEach((btn) => {
    btn.addEventListener("click", () => {
      const targetId = btn.dataset.target;

      document.querySelectorAll(".tab-button").forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");

      document.querySelectorAll(".tab-pane").forEach((pane) => {
        pane.classList.toggle("active", pane.id === targetId);
      });

      renderSummary();
    });
  });

  // init
  renderSummary();
});
