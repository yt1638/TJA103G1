document.addEventListener("DOMContentLoaded", () => {
  const unavailable = Array.isArray(window.UNAVAILABLE_SEAT_IDS)
    ? window.UNAVAILABLE_SEAT_IDS
    : [];
  const locked = Array.isArray(window.LOCKED_SEAT_IDS) ? window.LOCKED_SEAT_IDS : [];
  const selectedIds = Array.isArray(window.SELECTED_SEAT_IDS)
    ? window.SELECTED_SEAT_IDS
    : [];

  const expireAt = Number(window.EXPIRE_AT || 0);
  const maxSeats = Number(window.MAX_SEATS || 0);

  // 目前使用者選到的座位（含回填）
  const selected = []; // { seatId, row, seat }

  // ===== utils =====
  const $ = (sel) => document.querySelector(sel);
  const seatKey = (s) => `${s.row}${s.seat}`;

  function findSeatBtn(seatId) {
    return document.querySelector(`.seat-grid .seat[data-seat-id="${seatId}"]`);
  }

  function markUnavailable(seatId) {
    const btn = findSeatBtn(seatId);
    if (!btn) return;
    btn.classList.remove("available", "selected");
    btn.classList.add("unavailable");
    btn.disabled = true;
  }

  function updateSelectedUI() {
    const textEl = $("#selected-seats-text");
    const countEl = $("#selected-count");
    const summarySeatEl = $("#summary-seat");

    if (selected.length === 0) {
      if (textEl) textEl.textContent = "尚未選位";
      if (countEl) countEl.textContent = "0";
      if (summarySeatEl) summarySeatEl.textContent = "尚未選位";
      return;
    }

    const sorted = selected.slice().sort((a, b) => {
      if (a.row === b.row) return a.seat - b.seat;
      return a.row.localeCompare(b.row);
    });

    const text = sorted.map(seatKey).join(", ");
    if (textEl) textEl.textContent = text;
    if (countEl) countEl.textContent = String(selected.length);
    if (summarySeatEl) summarySeatEl.textContent = text;
  }

  function startCountdown() {
    const el = $("#countdown");
    if (!el) return;

    function formatMMSS(ms) {
      const totalSec = Math.max(0, Math.floor(ms / 1000));
      const m = Math.floor(totalSec / 60);
      const s = totalSec % 60;
      return String(m).padStart(2, "0") + ":" + String(s).padStart(2, "0");
    }

    function tick() {
      const left = expireAt - Date.now();
      if (left <= 0) {
        el.textContent = "00:00";
        alert("逾時未完成，請重新開始");
        window.location.href = "/order";
        return;
      }
      el.textContent = formatMMSS(left);
    }

    tick();
    setInterval(tick, 250);
  }

  // ===== restore selected seats (back from confirm) =====
  function restoreSelected() {
    if (!selectedIds || selectedIds.length === 0) return;

    for (let i = 0; i < selectedIds.length; i++) {
      const seatId = Number(selectedIds[i]);
      if (!Number.isFinite(seatId)) continue;

      const btn = findSeatBtn(seatId);
      if (!btn) continue;

      // 如果已售/損壞或被鎖
      if (btn.classList.contains("unavailable")) continue;

      const row = btn.dataset.row;
      const seatNo = Number(btn.dataset.seat);
      if (!row || !Number.isFinite(seatNo)) continue;

      //避免重複塞
      let exists = false;
      for (let j = 0; j < selected.length; j++) {
        if (selected[j].seatId === seatId) {
          exists = true;
          break;
        }
      }
      if (exists) continue;

      btn.classList.remove("available");
      btn.classList.add("selected");
      selected.push({ seatId: seatId, row: row, seat: seatNo });
    }
  }

  // ===== init unavailable seats =====
  unavailable.forEach(markUnavailable);
  locked.forEach(markUnavailable);
  
  // ===== highlight conflict seat (if any) =====
  const conflictSeatId = Number(window.CONFLICT_SEAT_ID || 0);
  if (Number.isFinite(conflictSeatId) && conflictSeatId > 0) {
    const btn = findSeatBtn(conflictSeatId);
    if (btn) {
      // 確保它是不可選樣式（通常衝突座位會是 locked）
      // 這行可留可不留，看你是否想強制灰掉
      // markUnavailable(conflictSeatId);

      btn.classList.add("conflict");
      btn.scrollIntoView({ behavior: "smooth", block: "center" });
    }
  }

  // 先回填舊座位，再綁定點擊（避免UI被後面覆蓋）
  restoreSelected();

  // 如果票數變動導致已選座位 > maxSeats，直接砍到剩maxSeats，避免卡住
  if (Number.isFinite(maxSeats) && maxSeats > 0 && selected.length > maxSeats) {
    // 多的先取消（從最後開始砍）
    while (selected.length > maxSeats) {
      const last = selected.pop();
      const btn = findSeatBtn(last.seatId);
      if (btn && !btn.classList.contains("unavailable")) {
        btn.classList.remove("selected");
        btn.classList.add("available");
      }
    }
  }

  // ===== bind seat click =====
  document.querySelectorAll(".seat-grid .seat").forEach((btn) => {
    btn.addEventListener("click", () => {
      if (btn.disabled || btn.classList.contains("unavailable")) return;

      const seatId = Number(btn.dataset.seatId);
      const row = btn.dataset.row;
      const seatNo = Number(btn.dataset.seat);

      if (!seatId || !row || !Number.isFinite(seatNo)) return;

      // toggle off
      if (btn.classList.contains("selected")) {
        btn.classList.remove("selected");
        btn.classList.add("available");
        const idx = selected.findIndex((s) => s.seatId === seatId);
        if (idx >= 0) selected.splice(idx, 1);
        updateSelectedUI();
        return;
      }

      // limit
      if (!Number.isFinite(maxSeats) || maxSeats <= 0) {
        alert("票數異常，請回上一頁重新選票");
        return;
      }
      if (selected.length >= maxSeats) {
        alert(`最多只能選 ${maxSeats} 個座位`);
        return;
      }

      // select on
      btn.classList.remove("available");
      btn.classList.add("selected");
      selected.push({ seatId, row, seat: seatNo });
      updateSelectedUI();
    });
  });

  // ===== back =====
  $("#back-btn")?.addEventListener("click", () => {
    window.location.href = "/order";
  });

  // ===== next: POST form =====
  $("#confirm-seat-btn")?.addEventListener("click", () => {
    if (selected.length !== maxSeats) {
      alert(`請選滿 ${maxSeats} 個座位`);
      return;
    }

    // 逗號字串，後端用split
    const idsCsv = selected
      .slice()
      .sort((a, b) => (a.row === b.row ? a.seat - b.seat : a.row.localeCompare(b.row)))
      .map((s) => s.seatId)
      .join(",");

    const input = $("#seatIdsCsv");
    if (input) input.value = idsCsv;

    const form = $("#seatNextForm");
    if (form) form.submit();
  });

  // ===== run =====
  updateSelectedUI();
  startCountdown();
});
