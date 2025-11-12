// 🟦 Page Navigation
function showPage(pageId) {
  document.querySelectorAll("section").forEach(sec => sec.classList.remove("active"));
  const el = document.getElementById(pageId);
  if (el) el.classList.add("active");
  window.scrollTo(0, 0);
}

// 🟩 Handle File Upload & Encryption
const sendForm = document.getElementById('sendForm');
if (sendForm) {
  sendForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(sendForm);
    const msg = document.getElementById('sendMsg');
    msg.textContent = "⏳ Uploading and encrypting your file securely...";
    msg.style.color = "#004aad";

    try {
      const response = await fetch('/process-folder', {
        method: 'POST',
        body: formData
      });

      // Try parse JSON, fallback to plain text
      let data;
      const ct = response.headers.get("content-type") || "";
      if (ct.includes("application/json")) {
        data = await response.json();
      } else {
        // some servers return JSON string without header; try parse or read text
        const text = await response.text();
        try {
          data = JSON.parse(text);
        } catch (_) {
          data = { status: "ok", message: text };
        }
      }

      // Normalize response shape
      const status = (data && (data.status || data.result || "").toString().toLowerCase()) || (response.ok ? "success" : "failed");
      const message = (data && (data.message || data.result || JSON.stringify(data))) || (response.ok ? "Done" : "Failed");

      if (status === "success" || response.ok) {
        msg.textContent = "✅ " + message;
        msg.style.color = "green";
      } else {
        msg.textContent = "❌ " + message;
        msg.style.color = "red";
      }
    } catch (error) {
      console.error("Upload error:", error);
      msg.textContent = "❌ Upload or encryption failed. Please try again.";
      msg.style.color = "red";
    }
  });
}

// 🟥 Handle File Download & Decryption
const downloadForm = document.getElementById('downloadForm');
if (downloadForm) {
  downloadForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(downloadForm);
    const msg = document.getElementById('downloadMsg');
    msg.textContent = "⏳ Downloading and decrypting your file...";
    msg.style.color = "#004aad";

    try {
      const fileName = formData.get('file_name');
      if (!fileName) {
        msg.textContent = "❌ Enter the encrypted filename (e.g., file.xlsx.enc)";
        msg.style.color = "red";
        return;
      }

      // NOTE: server expects file_name as a query param
      const url = `/download?file_name=${encodeURIComponent(fileName)}`;

      const response = await fetch(url, {
        method: 'POST'
      });

      if (!response.ok) {
        // try to read error message
        let errMsg = response.statusText || "File not found or decryption failed.";
        try { errMsg = await response.text(); } catch(_) {}
        msg.textContent = "❌ " + errMsg;
        msg.style.color = "red";
        return;
      }

      // Convert response to Blob for download
      const blob = await response.blob();
      const filename = fileName.replace(".enc", "");
      const link = document.createElement('a');
      link.href = window.URL.createObjectURL(blob);
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      link.remove();

      msg.textContent = "✅ File decrypted and downloaded successfully!";
      msg.style.color = "green";
      setTimeout(() => msg.textContent = "", 6000);

    } catch (error) {
      console.error("Download error:", error);
      msg.textContent = "❌ Download or decryption failed. Please try again.";
      msg.style.color = "red";
    }
  });
}

// 🟨 Optional: Simple Drag-and-Drop Effect for Upload Box
const uploadBox = document.querySelector('.upload-box');
if (uploadBox) {
  uploadBox.addEventListener('dragover', (e) => {
    e.preventDefault();
    uploadBox.style.background = "#f1f7ff";
  });

  uploadBox.addEventListener('dragleave', () => {
    uploadBox.style.background = "transparent";
  });

  uploadBox.addEventListener('drop', (e) => {
    e.preventDefault();
    uploadBox.style.background = "transparent";
    const fileInput = document.getElementById('fileInput');
    if (fileInput) fileInput.files = e.dataTransfer.files;
  });
}

// 🟦 Optional: Smooth scroll to top on navigation
window.addEventListener('load', () => {
  document.querySelectorAll('.menu a').forEach(link => {
    link.addEventListener('click', () => window.scrollTo({ top: 0, behavior: 'smooth' }));
  });
});
