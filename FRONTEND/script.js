
/* =========================================================
   SMARTCODE TUTOR — FRONTEND / SPRING BOOT INTEGRATION
   Backend: http://localhost:8080
   ========================================================= */

document.addEventListener("DOMContentLoaded", function () {
  if (window.lucide) window.lucide.createIcons();

  // CONFIGURATION
  var API_BASE_URL = "http://localhost:8080";

  var currentUser = null;
  var apiCredentials = null;
  var translationHistory = [];
  var authMode = "in";
  var translating = false;
  var latestExplanations = [];
  var latestWarnings = [];
  var activeResultTab = "explain";

  var $ = function (selector) {
    return document.querySelector(selector);
  };

  var $$ = function (selector) {
    return Array.from(document.querySelectorAll(selector));
  };

  var byId = function (id) {
    return document.getElementById(id);
  };

  // TOAST
  function showToast(message) {
    var existing = byId("smartcode-toast");
    if (existing) existing.remove();

    var toast = document.createElement("div");
    toast.id = "smartcode-toast";
    toast.textContent = message;

    Object.assign(toast.style, {
      position: "fixed",
      bottom: "24px",
      left: "50%",
      transform: "translateX(-50%)",
      zIndex: "99999",
      padding: "12px 20px",
      borderRadius: "10px",
      background: "#242424",
      color: "#fff",
      boxShadow: "0 6px 24px rgba(0,0,0,.2)",
      fontSize: "14px",
      maxWidth: "90%",
      textAlign: "center"
    });

    document.body.appendChild(toast);
    window.setTimeout(function () {
      toast.remove();
    }, 3000);
  }

  // API RESPONSE HANDLING
 
async function readResponse(response) {
  var data;

  try {
    data = await response.json();
  } catch (error) {
    throw new Error("The server returned an invalid response.");
  }

  if (!response.ok) {
    throw new Error(
      data.message || data.error || "Request failed."
    );
  }

  // A warning does not necessarily mean translation failed.
  // Return the response so the UI can display translated code
  // and any warnings supplied by the backend.
  return data;
}

  function getAuthHeaders() {
    if (!apiCredentials) return {};

    return {
      Authorization:
        "Basic " +
        btoa(
          apiCredentials.username + ":" + apiCredentials.password
        )
    };
  }

  // NAVIGATION
  function navigateTo(target) {
    if (!target) return;

    if (target === "profile-page" && !currentUser) {
      openAuth("in");
      showToast("Please sign in to view your profile.");
      return;
    }

    $$(".view").forEach(function (view) {
      view.classList.toggle("active", view.id === target);
    });

    $$(".nav-btn").forEach(function (button) {
      button.classList.toggle(
        "active",
        button.getAttribute("data-target") === target
      );
    });

    if (target === "profile-page") {
      loadTranslationHistory().catch(function (error) {
        console.error("History loading failed:", error);
        showToast("Could not refresh translation history.");
      });
    }

    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  $$("[data-target]").forEach(function (element) {
    element.addEventListener("click", function () {
      navigateTo(element.getAttribute("data-target"));
    });
  });

  // AUTHENTICATION ELEMENTS
  var overlay = byId("authOverlay");
  var authForm = byId("authForm");
  var authError = byId("authError");
  var authHeading = byId("authHeading");
  var authSubmit = byId("authSubmit");
  var authName = byId("authName");
  var authEmail = byId("authEmail");
  var authPass = byId("authPass");
  var nameField = byId("nameField");

  var trigger = byId("authTrigger");
  var signupTrigger = byId("signupTrigger");
  var profileTrigger = byId("profileTrigger");
  var pageLogoutTrigger = byId("pageLogoutTrigger");

  var profilePageName = byId("profilePageName");
  var profilePageDetailName = byId("profilePageDetailName");
  var profilePageDetailEmail = byId("profilePageDetailEmail");
  var profilePageBadge = byId("profilePageBadge");
  var profileHistoryList = byId("profileHistoryList");

  function showAuthError(message) {
    if (!authError) return;
    authError.textContent = message;
    authError.style.display = "block";
  }

  function clearAuthError() {
    if (!authError) return;
    authError.textContent = "";
    authError.style.display = "none";
  }

  function openAuth(mode) {
    setAuthMode(mode || "in");

    if (overlay) {
      overlay.classList.add("active");
      overlay.setAttribute("aria-hidden", "false");
    }

    clearAuthError();

    window.setTimeout(function () {
      if (authEmail) authEmail.focus();
    }, 100);
  }

  function closeAuth() {
    if (!overlay) return;
    overlay.classList.remove("active");
    overlay.setAttribute("aria-hidden", "true");
    clearAuthError();
  }

  function setAuthMode(mode) {
    authMode = mode === "up" ? "up" : "in";

    $$(".auth-tab").forEach(function (tab) {
      var tabMode = tab.dataset.mode || tab.dataset.authMode;

      if (!tabMode) {
        var label = tab.textContent.trim().toLowerCase();
        tabMode =
          label.includes("sign up") || label.includes("signup")
            ? "up"
            : "in";
      }

      tab.classList.toggle("active", tabMode === authMode);
    });

    if (nameField) {
      nameField.style.display = authMode === "up" ? "" : "none";
    }

    if (authHeading) {
      authHeading.textContent =
        authMode === "up" ? "Create your account" : "Welcome back";
    }

    if (authSubmit) {
      authSubmit.textContent =
        authMode === "up" ? "Create Account" : "Sign In";
    }

    if (authPass) {
      authPass.autocomplete =
        authMode === "up" ? "new-password" : "current-password";
    }

    if (authEmail) {
      authEmail.placeholder =
        authMode === "up"
          ? "you@college.edu"
          : "Username or email";
    }

    clearAuthError();
  }

  $$(".auth-tab").forEach(function (tab) {
    tab.addEventListener("click", function () {
      var mode = tab.dataset.mode || tab.dataset.authMode;

      if (!mode) {
        mode = tab.textContent.trim().toLowerCase().includes("sign up")
          ? "up"
          : "in";
      }

      setAuthMode(mode);
    });
  });

  if (trigger) {
    trigger.addEventListener("click", function () {
      if (currentUser) {
        logoutUser();
      } else {
        openAuth("in");
      }
    });
  }

  if (signupTrigger) {
    signupTrigger.addEventListener("click", function () {
      openAuth("up");
    });
  }

  if (profileTrigger) {
    profileTrigger.addEventListener("click", function () {
      navigateTo("profile-page");
    });
  }

  if (overlay) {
    overlay.addEventListener("click", function (event) {
      if (event.target === overlay) closeAuth();
    });
  }

  var authClose = byId("authClose");
  if (authClose) authClose.addEventListener("click", closeAuth);

  document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") closeAuth();
  });

  // SIGNUP AND LOGIN
  if (authForm) {
    authForm.addEventListener("submit", async function (event) {
      event.preventDefault();
      clearAuthError();

      var emailOrUsername = authEmail ? authEmail.value.trim() : "";
      var password = authPass ? authPass.value : "";
      var username = authName ? authName.value.trim() : "";
      var isSignup = authMode === "up";

      if (isSignup) {
        if (username.length < 3 || username.length > 50) {
          showAuthError("Username must contain 3–50 characters.");
          return;
        }

        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailOrUsername)) {
          showAuthError("Please enter a valid email address.");
          return;
        }

        if (password.length < 8 || password.length > 72) {
          showAuthError("Password must contain 8–72 characters.");
          return;
        }
      } else {
        if (!emailOrUsername || !password) {
          showAuthError("Enter your username/email and password.");
          return;
        }
      }

      var originalButtonText = authSubmit
        ? authSubmit.textContent
        : "";

      if (authSubmit) {
        authSubmit.disabled = true;
        authSubmit.textContent = "Please wait...";
      }

      try {
        var endpoint = isSignup
          ? "/api/auth/register"
          : "/api/auth/login";

        var payload = isSignup
          ? {
              username: username,
              email: emailOrUsername,
              password: password
            }
          : {
              usernameOrEmail: emailOrUsername,
              password: password
            };

        var response = await fetch(API_BASE_URL + endpoint, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });

        var data = await readResponse(response);

        var canonicalUsername =
          data.username || (isSignup ? username : "");

        if (!canonicalUsername) {
          throw new Error(
            "The server did not return your username. Check the login API response."
          );
        }

        apiCredentials = {
          username: canonicalUsername,
          password: password
        };

        currentUser = {
          name: canonicalUsername,
          email: data.email || (isSignup ? emailOrUsername : "")
        };

        translationHistory = [];

        closeAuth();
        authForm.reset();

        renderAccountState();
        renderProfileHistory();

        showToast(
          data.message ||
          (isSignup ? "Account created successfully!" : "Welcome back!")
        );

        try {
          await loadTranslationHistory();
        } catch (historyError) {
          console.error("Could not load history:", historyError);
        }
      } catch (error) {
        console.error("Authentication error:", error);
        showAuthError(
          error.message ||
          "Could not connect. Make sure Spring Boot is running."
        );
      } finally {
        if (authSubmit) {
          authSubmit.disabled = false;
          authSubmit.textContent =
            originalButtonText ||
            (authMode === "up" ? "Create Account" : "Sign In");
        }
      }
    });
  }

  // ACCOUNT DISPLAY
  function renderAccountState() {
    if (trigger) {
      trigger.textContent = currentUser ? "Log out" : "Login";
    }

    if (signupTrigger) {
      signupTrigger.style.display = currentUser ? "none" : "";
    }

    if (profileTrigger) {
      profileTrigger.classList.toggle("hidden", !currentUser);
    }

    if (profilePageName) {
      profilePageName.textContent =
        currentUser ? currentUser.name : "Your Profile";
    }

    if (profilePageDetailName) {
      profilePageDetailName.textContent =
        currentUser ? currentUser.name : "Not signed in";
    }

    if (profilePageDetailEmail) {
      profilePageDetailEmail.textContent =
        currentUser ? currentUser.email : "";
    }

    if (profilePageBadge) {
      profilePageBadge.textContent = currentUser
        ? currentUser.name.charAt(0).toUpperCase()
        : "A";
    }

    if (pageLogoutTrigger) {
      pageLogoutTrigger.style.display = currentUser ? "" : "none";
    }
  }

  function logoutUser() {
    // Session credentials remain in memory only.
    currentUser = null;
    apiCredentials = null;
    translationHistory = [];
    latestExplanations = [];
    latestWarnings = [];

    navigateTo("home");
    renderAccountState();
    renderProfileHistory();
    showToast("Logged out successfully.");
  }

  if (pageLogoutTrigger) {
    pageLogoutTrigger.addEventListener("click", logoutUser);
  }

  // TRANSLATION HISTORY
  async function loadTranslationHistory() {
    if (!currentUser || !apiCredentials) {
      translationHistory = [];
      renderProfileHistory();
      return;
    }

    var response = await fetch(API_BASE_URL + "/api/history", {
      method: "GET",
      headers: getAuthHeaders()
    });

    var records = await readResponse(response);
    if (!Array.isArray(records)) records = [];

    translationHistory = records.map(function (record) {
      var translatedCode = record.translatedCode || "";
      var preview = translatedCode
        .split("\n")
        .slice(0, 2)
        .join(" ")
        .trim();

      if (preview.length > 80) {
        preview = preview.substring(0, 77) + "...";
      }

      return {
        id: record.id,
        source: record.sourceLanguage || "Unknown",
        target: record.targetLanguage || "Unknown",
        preview: preview || "Translation saved",
        time: record.createdAt
          ? new Date(record.createdAt).toLocaleString()
          : "Saved translation"
      };
    });

    renderProfileHistory();
  }

  function renderProfileHistory() {
    if (!profileHistoryList) return;

    profileHistoryList.replaceChildren();

    if (!currentUser) {
      var signedOut = document.createElement("li");
      signedOut.className = "empty-history";
      signedOut.textContent = "Sign in to view your translation history.";
      profileHistoryList.appendChild(signedOut);
      return;
    }

    if (!translationHistory.length) {
      var empty = document.createElement("li");
      empty.className = "empty-history";
      empty.textContent =
        "No translations yet. Your saved translations will appear here.";
      profileHistoryList.appendChild(empty);
      return;
    }

    translationHistory.forEach(function (item) {
      var card = document.createElement("li");
      card.className = "history-item";

      var heading = document.createElement("strong");
      heading.textContent = item.source + " → " + item.target;

      var preview = document.createElement("div");
      preview.textContent = item.preview;

      var time = document.createElement("small");
      time.textContent = item.time;

      card.append(heading, preview, time);
      profileHistoryList.appendChild(card);
    });
  }

  // LANGUAGE SAMPLES
  var languageSamples = {
    Java:
      'public class Main {\n' +
      '    public static void main(String[] args) {\n' +
      '        int number = 10;\n' +
      '        System.out.println("Number: " + number);\n' +
      '    }\n' +
      '}',

    "C++":
      '#include <iostream>\n' +
      'using namespace std;\n\n' +
      'int main() {\n' +
      '    int number = 10;\n' +
      '    cout << "Number: " << number << endl;\n' +
      '    return 0;\n' +
      '}',

    C:
      '#include <stdio.h>\n\n' +
      'int main() {\n' +
      '    int number = 10;\n' +
      '    printf("Number: %d\\n", number);\n' +
      '    return 0;\n' +
      '}',

    Python:
      'number = 10\n' +
      'print(f"Number: {number}")'
  };

  var srcCode = byId("srcCode");
  var srcLang = byId("srcLang");
  var tgtLang = byId("tgtLang");

  function updateSample() {
    if (!srcCode || !srcLang) return;

    if (languageSamples[srcLang.value]) {
      srcCode.value = languageSamples[srcLang.value];
    }

    var sourceLabel = byId("sourceLanguageLabel");
    if (sourceLabel) sourceLabel.textContent = srcLang.value;
  }

  if (srcLang) {
    srcLang.addEventListener("change", updateSample);
  }

  function updateLanguageLabels() {
    if (srcLang && byId("sourceLanguageLabel")) {
      byId("sourceLanguageLabel").textContent = srcLang.value;
    }

    if (tgtLang && byId("targetLanguageLabel")) {
      byId("targetLanguageLabel").textContent = tgtLang.value;
    }
  }

  if (tgtLang) {
    tgtLang.addEventListener("change", updateLanguageLabels);
  }

  var swapBtn = byId("swapBtn");

  if (swapBtn && srcLang && tgtLang) {
    swapBtn.addEventListener("click", function () {
      var oldSource = srcLang.value;
      srcLang.value = tgtLang.value;
      tgtLang.value = oldSource;

      updateSample();
      updateLanguageLabels();
    });
  }

  // CODE OUTPUT
  function escapeHtml(value) {
    return String(value == null ? "" : value)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  function renderTarget(code, language) {
    var targetBody = byId("targetBody");
    if (!targetBody) return;

    var languageClasses = {
      Java: "language-java",
      "C++": "language-cpp",
      C: "language-c",
      Python: "language-python"
    };

    var languageClass =
      languageClasses[language] || "language-clike";

    targetBody.innerHTML =
      '<pre style="margin:0;white-space:pre-wrap;"><code class="' +
      languageClass +
      '">' +
      escapeHtml(code) +
      "</code></pre>";

    if (window.Prism) {
      var codeElement = targetBody.querySelector("code");
      if (codeElement) window.Prism.highlightElement(codeElement);
    }
  }

  // EXPLANATIONS AND WARNINGS
  function renderResultTab(tabName) {
    activeResultTab = tabName === "warnings" ? "warnings" : "explain";

    var panel = byId("explainPanel");
    if (!panel) return;

    $$(".etab").forEach(function (tab) {
      var selected =
        activeResultTab === "explain"
          ? tab.dataset.tab === "explain"
          : tab.dataset.tab === "warn";

      tab.classList.toggle("active", selected);
    });

    panel.replaceChildren();

    if (activeResultTab === "explain") {
      if (!latestExplanations.length) {
        var empty = document.createElement("div");
        empty.className = "exp-empty";
        empty.textContent =
          "No explanations available. Run a translation first.";
        panel.appendChild(empty);
        return;
      }

      latestExplanations.forEach(function (item, index) {
        var row = document.createElement("div");
        row.className = "exp-row";

        var line = document.createElement("span");
        line.className = "ln";
        line.textContent = String(index + 1).padStart(2, "0");

        var body = document.createElement("div");
        body.className = "exp-body";

        var description = document.createElement("p");
        description.className = "note";
        description.textContent =
          typeof item === "string"
            ? item
            : item && (item.explanation || item.message || item.description)
              ? (item.explanation || item.message || item.description)
              : JSON.stringify(item);

        body.appendChild(description);
        row.append(line, body);
        panel.appendChild(row);
      });

      return;
    }

    if (!latestWarnings.length) {
      var noWarnings = document.createElement("div");
      noWarnings.className = "exp-empty";
      noWarnings.textContent = "No warnings reported.";
      panel.appendChild(noWarnings);
      return;
    }

    latestWarnings.forEach(function (warning) {
      var row = document.createElement("div");
      row.className = "exp-row warn-row";

      var icon = document.createElement("span");
      icon.textContent = "⚠";

      var body = document.createElement("div");
      body.className = "exp-body";

      var message = document.createElement("p");
      message.className = "note";
      message.textContent =
        typeof warning === "string"
          ? warning
          : warning && (warning.message || warning.description)
            ? (warning.message || warning.description)
            : JSON.stringify(warning);

      body.appendChild(message);
      row.append(icon, body);
      panel.appendChild(row);
    });
  }

  function resetResultPanels() {
    latestExplanations = [];
    latestWarnings = [];

    var warnCount = byId("warnCount");
    if (warnCount) warnCount.textContent = "0";

    renderResultTab("explain");
  }

  $$(".etab").forEach(function (tab) {
    tab.addEventListener("click", function () {
      renderResultTab(tab.dataset.tab === "warn" ? "warnings" : "explain");
    });
  });

  // TRANSLATION API
  var translateBtn = byId("translateBtn");

  if (translateBtn) {
    translateBtn.addEventListener("click", async function () {
      if (translating) return;

      var code = srcCode ? srcCode.value : "";
      var sourceLanguage = srcLang ? srcLang.value : "";
      var targetLanguage = tgtLang ? tgtLang.value : "";
      var translateLabel = byId("translateLabel");

      if (!code.trim()) {
        showToast("Enter some code to translate.");
        return;
      }

      if (sourceLanguage === targetLanguage) {
        showToast("Choose two different programming languages.");
        return;
      }

      if (!currentUser || !apiCredentials) {
        showToast("Please sign in to translate code.");
        openAuth("in");
        return;
      }

      translating = true;
      translateBtn.disabled = true;

      var previousLabel = translateLabel
        ? translateLabel.textContent
        : "Translate";

      if (translateLabel) translateLabel.textContent = "Translating...";

      try {
        var response = await fetch(API_BASE_URL + "/api/code/translate", {
          method: "POST",
          headers: Object.assign(
            { "Content-Type": "application/json" },
            getAuthHeaders()
          ),
          body: JSON.stringify({
            code: code,
            sourceLanguage: sourceLanguage,
            targetLanguage: targetLanguage
          })
        });

        var result = await readResponse(response);

        renderTarget(result.translatedCode || "", targetLanguage);

        latestExplanations = Array.isArray(result.explanations)
          ? result.explanations
          : [];

        latestWarnings = Array.isArray(result.warnings)
          ? result.warnings
          : [];

        var warnCount = byId("warnCount");
        if (warnCount) {
          warnCount.textContent = String(latestWarnings.length);
        }

        renderResultTab("explain");
        updateLanguageLabels();

        try {
          await loadTranslationHistory();
        } catch (historyError) {
          console.error("History refresh failed:", historyError);
        }

        showToast("Translation complete!");
      } catch (error) {
        console.error("Translation failed:", error);
        showToast(error.message || "Could not connect to the backend.");
      } finally {
        translating = false;
        translateBtn.disabled = false;

        if (translateLabel) {
          translateLabel.textContent = previousLabel;
        }
      }
    });
  }

  // CLEAR
  var clearBtn = byId("clearBtn");

  if (clearBtn) {
    clearBtn.addEventListener("click", function () {
      if (srcCode) srcCode.value = "";

      var targetBody = byId("targetBody");
      if (targetBody) {
        targetBody.innerHTML =
          '<div class="empty-target">' +
          '<span>Translated code will appear here</span>' +
          "</div>";
      }

      resetResultPanels();
      showToast("Editor cleared.");
    });
  }

  // COPY
  
  // COPY — with a fallback for browsers that block clipboard access
  var copyBtn = byId("copyBtn");

  if (copyBtn) {
    copyBtn.addEventListener("click", async function () {
      var targetBody = byId("targetBody");
      var codeElement = targetBody
        ? targetBody.querySelector("code")
        : null;

      var code = codeElement ? codeElement.textContent : "";

      if (!code.trim()) {
        showToast("There is no translated code to copy.");
        return;
      }

      try {
        if (navigator.clipboard && window.isSecureContext) {
          await navigator.clipboard.writeText(code);
        } else {
          var temporary = document.createElement("textarea");
          temporary.value = code;
          temporary.setAttribute("readonly", "");
          Object.assign(temporary.style, {
            position: "fixed",
            left: "-9999px",
            top: "0",
            opacity: "0"
          });

          document.body.appendChild(temporary);
          temporary.select();
          temporary.setSelectionRange(0, temporary.value.length);

          var copied = document.execCommand("copy");
          temporary.remove();

          if (!copied) {
            throw new Error("Browser blocked clipboard access.");
          }
        }

        showToast("Code copied!");
      } catch (error) {
        console.error("Clipboard error:", error);
        showToast("Copy failed. Select the translated code and press Ctrl+C.");
      }
    });
  }


  // DOWNLOAD
  var downloadBtn = byId("downloadBtn");

  if (downloadBtn) {
    downloadBtn.addEventListener("click", function () {
      var targetBody = byId("targetBody");
      var codeElement = targetBody
        ? targetBody.querySelector("code")
        : null;

      var code = codeElement ? codeElement.textContent : "";

      if (!code.trim()) {
        showToast("There is no translated code to download.");
        return;
      }

      var extensions = {
        Java: "java",
        "C++": "cpp",
        C: "c",
        Python: "py"
      };

      var language = tgtLang ? tgtLang.value : "txt";
      var extension = extensions[language] || "txt";
      var blob = new Blob([code], {
        type: "text/plain;charset=utf-8"
      });

      var url = URL.createObjectURL(blob);
      var link = document.createElement("a");

      link.href = url;
      link.download = "translated_code." + extension;
      document.body.appendChild(link);
      link.click();
      link.remove();

      window.setTimeout(function () {
        URL.revokeObjectURL(url);
      }, 1000);

      showToast("Download started.");
    });
  }

  // INITIALIZE
  renderAccountState();
  renderProfileHistory();
  updateLanguageLabels();
  resetResultPanels();

  // Authentication credentials are intentionally not stored
  // in localStorage. Sign in again after refreshing the page.
});
