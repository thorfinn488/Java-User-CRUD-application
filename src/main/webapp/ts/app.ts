/**
 * app.ts
 * ------
 * All client-side interactivity for the User Management System,
 * written in TypeScript and compiled to js/app.js (see tsconfig.json).
 *
 * Responsibilities:
 *  1. Replace the native browser confirm() on Delete with a styled
 *     Bootstrap modal (deleteUser()).
 *  2. Auto-dismiss success/error alerts after a few seconds (toast-style).
 *  3. Live, real-time form validation feedback (Bootstrap "is-valid" /
 *     "is-invalid" classes) for name, email, password, and phone fields.
 *  4. A tiny password-strength indicator on the Add User form.
 *  5. A shrinking / elevated navbar on scroll for a modern feel.
 *
 * No external TS libraries are used - only the built-in DOM lib types,
 * so this compiles standalone with `tsc` and needs no bundler.
 */

// ---------------------------------------------------------------------
// Types
// ---------------------------------------------------------------------

/** Shape of the small config each validated field needs. */
interface FieldValidator {
  input: HTMLInputElement | HTMLTextAreaElement;
  isValid: (value: string) => boolean;
  message: string;
  /** If true, an empty value is treated as valid (field is optional). */
  optional?: boolean;
}

// ---------------------------------------------------------------------
// Regex patterns (mirrors the server-side validation in UserServlet)
// ---------------------------------------------------------------------

const EMAIL_REGEX: RegExp = /^[\w.+-]+@[\w-]+\.[a-zA-Z]{2,}$/;
const PHONE_REGEX: RegExp = /^\d{7,15}$/;

// ---------------------------------------------------------------------
// 1. Delete confirmation modal
// ---------------------------------------------------------------------

/**
 * Called from an inline onclick="return confirmDelete(event, 'Jane Doe', 3)"
 * on each Delete button. Instead of the plain native confirm(), it opens a
 * styled Bootstrap modal and only navigates to the delete URL if the user
 * confirms inside that modal.
 */
function confirmDelete(event: MouseEvent, userName: string, userId: number): boolean {
  event.preventDefault();

  const modalEl = document.getElementById("deleteModal");
  const nameSpan = document.getElementById("deleteUserName");
  const confirmBtn = document.getElementById("confirmDeleteBtn") as HTMLAnchorElement | null;

  if (!modalEl || !nameSpan || !confirmBtn) {
    // Fallback in case the modal markup is missing for some reason.
    window.location.href = `UserServlet?action=delete&id=${userId}`;
    return false;
  }

  nameSpan.textContent = userName;
  confirmBtn.href = `UserServlet?action=delete&id=${userId}`;

  // Bootstrap 5's JS bundle attaches bootstrap.Modal to the window object.
  const bootstrapModal = new (window as any).bootstrap.Modal(modalEl);
  bootstrapModal.show();

  return false;
}

// Expose to inline HTML onclick handlers (TS modules are function-scoped by file,
// so this must be attached to window explicitly).
(window as any).confirmDelete = confirmDelete;

// ---------------------------------------------------------------------
// 2. Auto-dismiss alert banners
// ---------------------------------------------------------------------

function autoDismissAlerts(): void {
  const alerts: NodeListOf<HTMLElement> = document.querySelectorAll(".alert[data-auto-dismiss]");

  alerts.forEach((alertEl: HTMLElement) => {
    window.setTimeout(() => {
      alertEl.classList.add("fade-out");
      window.setTimeout(() => alertEl.remove(), 500);
    }, 4000);
  });
}

// ---------------------------------------------------------------------
// 3. Live field validation
// ---------------------------------------------------------------------

function applyValidationState(
  input: HTMLInputElement | HTMLTextAreaElement,
  valid: boolean,
  feedbackId: string,
  message: string
): void {
  const feedbackEl = document.getElementById(feedbackId);

  input.classList.remove("is-valid", "is-invalid");
  input.classList.add(valid ? "is-valid" : "is-invalid");

  if (feedbackEl) {
    feedbackEl.textContent = message;
  }
}

function wireLiveValidation(): void {
  const nameInput = document.getElementById("name") as HTMLInputElement | null;
  const emailInput = document.getElementById("email") as HTMLInputElement | null;
  const passwordInput = document.getElementById("password") as HTMLInputElement | null;
  const phoneInput = document.getElementById("phone") as HTMLInputElement | null;

  const passwordOptional: boolean = passwordInput?.dataset.optional === "true";

  const validators: FieldValidator[] = [];

  if (nameInput) {
    validators.push({
      input: nameInput,
      isValid: (v: string) => v.trim().length > 0,
      message: "Please enter a name.",
    });
  }

  if (emailInput) {
    validators.push({
      input: emailInput,
      isValid: (v: string) => EMAIL_REGEX.test(v.trim()),
      message: "Please enter a valid email address.",
    });
  }

  if (passwordInput) {
    validators.push({
      input: passwordInput,
      isValid: (v: string) => v.length >= 6,
      message: "Password must be at least 6 characters.",
      optional: passwordOptional,
    });
  }

  if (phoneInput) {
    validators.push({
      input: phoneInput,
      isValid: (v: string) => PHONE_REGEX.test(v.trim()),
      message: "Digits only, 7-15 numbers.",
      optional: true,
    });
  }

  validators.forEach((validator: FieldValidator) => {
    const feedbackId = `${validator.input.id}-feedback`;

    validator.input.addEventListener("input", () => {
      const value = validator.input.value;

      if (value.trim().length === 0 && validator.optional) {
        validator.input.classList.remove("is-valid", "is-invalid");
        return;
      }

      const valid = validator.isValid(value);
      applyValidationState(validator.input, valid, feedbackId, validator.message);

      if (validator.input === passwordInput) {
        updatePasswordStrength(value);
      }
    });
  });
}

// ---------------------------------------------------------------------
// 4. Password strength meter (small creative touch on Add User form)
// ---------------------------------------------------------------------

function updatePasswordStrength(password: string): void {
  const meter = document.getElementById("passwordStrengthBar");
  const label = document.getElementById("passwordStrengthLabel");
  if (!meter || !label) {
    return;
  }

  let score = 0;
  if (password.length >= 6) score += 1;
  if (password.length >= 10) score += 1;
  if (/[A-Z]/.test(password)) score += 1;
  if (/[0-9]/.test(password)) score += 1;
  if (/[^A-Za-z0-9]/.test(password)) score += 1;

  const levels: { width: string; className: string; text: string }[] = [
    { width: "5%", className: "bg-danger", text: "" },
    { width: "20%", className: "bg-danger", text: "Very weak" },
    { width: "40%", className: "bg-warning", text: "Weak" },
    { width: "60%", className: "bg-warning", text: "Fair" },
    { width: "80%", className: "bg-info", text: "Good" },
    { width: "100%", className: "bg-success", text: "Strong" },
  ];

  const level = levels[Math.min(score, levels.length - 1)];

  meter.style.width = password.length === 0 ? "0%" : level.width;
  meter.className = `progress-bar ${level.className}`;
  label.textContent = password.length === 0 ? "" : level.text;
}

// ---------------------------------------------------------------------
// 5. Elevated navbar on scroll
// ---------------------------------------------------------------------

function wireNavbarScrollEffect(): void {
  const navbar = document.querySelector<HTMLElement>(".navbar-custom");
  if (!navbar) {
    return;
  }

  window.addEventListener("scroll", () => {
    if (window.scrollY > 12) {
      navbar.classList.add("navbar-scrolled");
    } else {
      navbar.classList.remove("navbar-scrolled");
    }
  });
}

// ---------------------------------------------------------------------
// Bootstrap-style client-side "was-validated" guard on submit
// (prevents obviously-invalid forms from posting; server still re-checks)
// ---------------------------------------------------------------------

function wireFormGuard(): void {
  const forms: NodeListOf<HTMLFormElement> = document.querySelectorAll("form[data-validate]");

  forms.forEach((form: HTMLFormElement) => {
    form.addEventListener("submit", (e: SubmitEvent) => {
      if (!form.checkValidity()) {
        e.preventDefault();
        e.stopPropagation();
      }
      form.classList.add("was-validated");
    });
  });
}

// ---------------------------------------------------------------------
// Init
// ---------------------------------------------------------------------

document.addEventListener("DOMContentLoaded", () => {
  autoDismissAlerts();
  wireLiveValidation();
  wireNavbarScrollEffect();
  wireFormGuard();
});
