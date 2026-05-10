document.addEventListener('DOMContentLoaded', function () {
    const btn = document.getElementById('userMenuButton');
    const menu = document.getElementById('userMenu');
    if (btn && menu) {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            menu.classList.toggle('hidden');
        });
        document.addEventListener('click', function () {
            menu.classList.add('hidden');
        });
    }
});
