document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector(".login-form");

    if (form) {
        form.addEventListener("submit", (e) => {
            e.preventDefault();
            document.body.classList.add("fade-out");

            // Delay để hiển thị hiệu ứng mờ dần trước khi chuyển trang
            setTimeout(() => {
                form.submit();
            }, 700);
        });
    }
});
