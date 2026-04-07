document.addEventListener('DOMContentLoaded', async () => {
    const saveBtn = document.getElementById('saveProfileButton');
    if (!saveBtn) return;

    const status = document.getElementById('profileStatus');

    async function loadProfile() {
        try {
            if (status) status.textContent = 'Loading profile...';

            await window.ApiClient.ensureAuthenticated();
            const profile = await window.ProfileApi.getProfile();

            const email = document.getElementById('profileEmail');
            const firstName = document.getElementById('profileFirstName');
            const lastName = document.getElementById('profileLastName');
            const createdAt = document.getElementById('profileCreatedAt');

            if (email) email.value = profile.email || '';
            if (firstName) firstName.value = profile.firstName || '';
            if (lastName) lastName.value = profile.lastName || '';
            if (createdAt) {
                createdAt.textContent = profile.createdAt
                    ? new Date(profile.createdAt).toLocaleString()
                    : 'Unavailable';
            }

            if (status) status.textContent = 'Profile loaded.';
        } catch (error) {
            if (status) status.textContent = error.message || 'Unable to load profile.';
        }
    }

    async function saveProfile() {
        const payload = {
            email: document.getElementById('profileEmail')?.value.trim() || '',
            firstName: document.getElementById('profileFirstName')?.value.trim() || '',
            lastName: document.getElementById('profileLastName')?.value.trim() || ''
        };

        try {
            if (status) status.textContent = 'Saving profile...';

            await window.ProfileApi.updateProfile(payload);

            if (status) status.textContent = 'Profile updated successfully.';
        } catch (error) {
            if (status) status.textContent = error.message || 'Unable to save profile.';
        }
    }

    await loadProfile();
    saveBtn.addEventListener('click', saveProfile);
});