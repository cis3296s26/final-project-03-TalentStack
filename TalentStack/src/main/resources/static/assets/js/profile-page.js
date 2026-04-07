document.addEventListener('DOMContentLoaded', async () => {
    const email = document.getElementById('displayEmail');
    if (!email) return;

    const firstName = document.getElementById('displayFirstName');
    const lastName = document.getElementById('displayLastName');
    const createdAt = document.getElementById('displayCreatedAt');
    const status = document.getElementById('profileStatus');

    try {
        await window.ApiClient.ensureAuthenticated();
        const profile = await window.ProfileApi.getProfile();

        if (email) email.textContent = profile.email || 'Not Set';
        if (firstName) firstName.textContent = profile.firstName || 'Not Set';
        if (lastName) lastName.textContent = profile.lastName || 'Not Set';
        if (createdAt) {
            createdAt.textContent = profile.createdAt
                ? new Date(profile.createdAt).toLocaleString()
                : 'Unavailable';
        }

        if (status) status.textContent = 'Profile loaded.';
    } catch (error) {
        if (status) status.textContent = error.message || 'Unable to load profile.';
    }
});