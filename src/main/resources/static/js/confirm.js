// /js/confirm.js
document.addEventListener("DOMContentLoaded", function () {
  var expireAt = Number(window.EXPIRE_AT || 0);

  function formatMMSS(ms) {
    var totalSec = Math.max(0, Math.floor(ms / 1000));
    var m = Math.floor(totalSec / 60);
    var s = totalSec % 60;
    return String(m).padStart(2, "0") + ":" + String(s).padStart(2, "0");
  }

  function startCountdown() {
    var el = document.querySelector("#countdown");
    var checkoutBtn = document.querySelector("#checkout-btn");

    if (!el) return;
    if (!Number.isFinite(expireAt) || expireAt <= 0) {
      el.textContent = "--:--";
      if (checkoutBtn) checkoutBtn.disabled = true;
      alert("流程資訊遺失（expireAt 未帶入），請回到訂票頁重新操作。");
      return;
    }

    function tick() {
      var left = expireAt - Date.now();
      if (left <= 0) {
        el.textContent = "00:00";
        alert("逾時未完成，請重新選擇場次/座位");
        window.location.href = "/order";
        return;
      }
      el.textContent = formatMMSS(left);
    }

    tick();
    setInterval(tick, 250);
  }

  // 防止連點：用submit事件
  var form = document.querySelector("#submitForm");
  var checkoutBtn = document.querySelector("#checkout-btn");
  if (form && checkoutBtn) {
    form.addEventListener("submit", function () {
      checkoutBtn.disabled = true;
    });
  }

  startCountdown();
});
