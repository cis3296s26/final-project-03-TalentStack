document.addEventListener('DOMContentLoaded', () => {
    const dropdowns = document.querySelectorAll('.dropdown');

    dropdowns.forEach((dropdown) => {
        const btn = dropdown.querySelector('.dropbtn');
        const menu = dropdown.querySelector('.dropdown-content');

        if (!btn || !menu) return;

        btn.addEventListener('click', (event) => {
            event.stopPropagation();

            dropdowns.forEach((d) => {
                const current = d.querySelector('.dropdown-content');
                if (current && current !== menu) {
                    current.classList.remove('show');
                }
            });

            menu.classList.toggle('show');
        });
    });

    document.addEventListener('click', () => {
        dropdowns.forEach((dropdown) => {
            const menu = dropdown.querySelector('.dropdown-content');
            if (menu) {
                menu.classList.remove('show');
            }
        });
    });

    const saveBtn = document.getElementById('saveProfileButton');
    if (saveBtn && window.ProfileApi && window.loadProfile && window.saveProfile) {
        window.loadProfile();
        saveBtn.addEventListener('click', () => {
            window.saveProfile();
        });
    }

    if (document.getElementById('displayEmail') && window.ProfileApi) {
        window.ProfileApi.getProfile()
            .then((profile) => {
                const email = document.getElementById('displayEmail');
                const firstName = document.getElementById('displayFirstName');
                const lastName = document.getElementById('displayLastName');
                const createdAt = document.getElementById('displayCreatedAt');
                const status = document.getElementById('profileStatus');

                if (email) email.textContent = profile.email || 'Not Set';
                if (firstName) firstName.textContent = profile.firstName || 'Not Set';
                if (lastName) lastName.textContent = profile.lastName || 'Not Set';
                if (createdAt) {
                    createdAt.textContent = profile.createdAt
                        ? new Date(profile.createdAt).toLocaleString()
                        : 'Unavailable';
                }

                if (status) status.textContent = 'Profile loaded.';
            })
            .catch((error) => {
                const status = document.getElementById('profileStatus');
                if (status) status.textContent = error.message;
            });
    }
});