"use strict";
const EMAIL_REGEX = /^[\w.+-]+@[\w-]+\.[a-zA-Z]{2,}$/;
const PHONE_REGEX = /^\d{7,15}$/;
function confirmDelete(event, userName, userId) {
    event.preventDefault();
    const modalEl = document.getElementById("deleteModal");
    const nameSpan = document.getElementById("deleteUserName");
    const confirmBtn = document.getElementById("confirmDeleteBtn");
    if (!modalEl || !nameSpan || !confirmBtn) {
        window.location.href = `UserServlet?action=delete&id=${userId}`;
        return false;
    }
    nameSpan.textContent = userName;
    confirmBtn.href = `UserServlet?action=delete&id=${userId}`;
    const bootstrapModal = new window.bootstrap.Modal(modalEl);
    bootstrapModal.show();
    return false;
}
window.confirmDelete = confirmDelete;
function autoDismissAlerts() {
    const alerts = document.querySelectorAll(".alert[data-auto-dismiss]");
    alerts.forEach((alertEl) => {
        window.setTimeout(() => {
            alertEl.classList.add("fade-out");
            window.setTimeout(() => alertEl.remove(), 500);
        }, 4000);
    });
}
function applyValidationState(input, valid, feedbackId, message) {
    const feedbackEl = document.getElementById(feedbackId);
    input.classList.remove("is-valid", "is-invalid");
    input.classList.add(valid ? "is-valid" : "is-invalid");
    if (feedbackEl) {
        feedbackEl.textContent = message;
    }
}
function wireLiveValidation() {
    const nameInput = document.getElementById("name");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const phoneInput = document.getElementById("phone");
    const passwordOptional = passwordInput?.dataset.optional === "true";
    const validators = [];
    if (nameInput) {
        validators.push({
            input: nameInput,
            isValid: (v) => v.trim().length > 0,
            message: "Please enter a name.",
        });
    }
    if (emailInput) {
        validators.push({
            input: emailInput,
            isValid: (v) => EMAIL_REGEX.test(v.trim()),
            message: "Please enter a valid email address.",
        });
    }
    if (passwordInput) {
        validators.push({
            input: passwordInput,
            isValid: (v) => v.length >= 6,
            message: "Password must be at least 6 characters.",
            optional: passwordOptional,
        });
    }
    if (phoneInput) {
        validators.push({
            input: phoneInput,
            isValid: (v) => PHONE_REGEX.test(v.trim()),
            message: "Digits only, 7-15 numbers.",
            optional: true,
        });
    }
    validators.forEach((validator) => {
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
function updatePasswordStrength(password) {
    const meter = document.getElementById("passwordStrengthBar");
    const label = document.getElementById("passwordStrengthLabel");
    if (!meter || !label) {
        return;
    }
    let score = 0;
    if (password.length >= 6)
        score += 1;
    if (password.length >= 10)
        score += 1;
    if (/[A-Z]/.test(password))
        score += 1;
    if (/[0-9]/.test(password))
        score += 1;
    if (/[^A-Za-z0-9]/.test(password))
        score += 1;
    const levels = [
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
function wireNavbarScrollEffect() {
    const navbar = document.querySelector(".navbar-custom");
    if (!navbar) {
        return;
    }
    window.addEventListener("scroll", () => {
        if (window.scrollY > 12) {
            navbar.classList.add("navbar-scrolled");
        }
        else {
            navbar.classList.remove("navbar-scrolled");
        }
    });
}
function wireFormGuard() {
    const forms = document.querySelectorAll("form[data-validate]");
    forms.forEach((form) => {
        form.addEventListener("submit", (e) => {
            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }
            form.classList.add("was-validated");
        });
    });
}
document.addEventListener("DOMContentLoaded", () => {
    autoDismissAlerts();
    wireLiveValidation();
    wireNavbarScrollEffect();
    wireFormGuard();
});
