document.addEventListener('DOMContentLoaded', () =>{
    const dropdowns = document.querySelectorAll('.dropdown');


    dropdowns.forEach(dropdown => {
        const btn = dropdown.querySelector('.dropbtn');
        const menu = dropdown.querySelector('.dropdown-content');

        btn.addEventListener('click', (e) => {
            e.stopPropagation();

            // close all other dropdowns (to fix where they're both open)
            dropdowns.forEach(d => {
                const m = d.querySelector('.dropdown-content');
                if (m !== menu) {
                    m.classList.remove('show');
                }   
            });

            // toggle current one selected
            menu.classList.toggle('show');
        });
    });

    // close all dropdowns when clicked outside (elsewhere on page)
    document.addEventListener('click', () => {
        dropdowns.forEach(d => {
        d.querySelector('.dropdown-content').classList.remove('show');
        });
    });

    // SETTINGS.html functionalities (runs only in settings)
    if(document.querySelector('.profile-container')){
        // global functions for onclick
        window.resetPic = resetPic;
        window.saveBasicInfo = saveBasicInfo;
        window.addTag - addTag;

        // profile picture upload
        const picUpload = document.getElementById('picUpload');
        if(picUpload){
            picUpload.addEventListener('change', (e) => {
                const file = event.target.files[0];
                if(file){
                    const reader = new FileReader();
                    reader.onload = (event) => {
                        document.getElementById('profilePicture').src = e.target.result;
                        localStorage.setItem('profilePic', event.target.result); // share with profile.html
                    };
                    reader.readAsDataURL(file);
                }
            });
        }

        // reset profile picture to default
        function resetPic(){
            document.getElementById('profilePicture').src = '../resources/defaultProfilePic.jpg';
            localStorage.removeItem('profilePic');
        }

        // save basic information
        function saveBasicInfo(){
            const info = {
                name: document.getElementById('fullName').value,
                status: document.getElementById('status').value,
                location: document.getElementById('location').value
            };
            localStorage.setItem('profileInfo', JSON.stringify(info));
            alert('Profile Saved!');
        }

        // add tags & sections
        // TBD

        // load data into forms
        const savedInfo = localStorage.getItem('profileInfo');
        if(savedInfo){
            const info = JSON.parse(savedInfo);
            document.getElementById('fullName').value = info.name || '';
            document.getElementById('status').value = info.status || 'student';
            document.getElementById('location').value = info.location || '';
        }

        const savedPic = localStorage.getItem('profilePic');
        if(savedPic){
            document.getElementById('profilePicture').src = savedPic;
        }
    }

    // PROFILE.html functionalities (runs only in profile)
    if(document.querySelector('profile-display')){
        const savedInfo = localStorage.getItem('profileInfo');
        const savedPic = localStorage.getItem('profilePic');
        // const savedTags = localStorage.getItem('profileTags')

        // hide locked notice, display data if it exists
        const lockedNotice = document.getElementById('lockedNotice');
        if(savedInfo || savedPic){
            if(lockedNotice){
                lockedNotice.style.display = none;
            }

            // load profile picture
            const profilePic = document.getElementById('profilePicture') || document.getElementById('profileDisplayPic');
            if(savedPic && profilePic){
                profilePic.src = savedPic;
            }

            // load basic info
            if (savedInfo) {
                const info = JSON.parse(savedInfo);
                const nameEl = document.getElementById('displayName');
                const statusEl = document.getElementById('displayStatus');
                const locationEl = document.getElementById('displayLocation');
                const infoSection = document.getElementById('profileInfoSection');

                if (nameEl) nameEl.textContent = info.name || 'Not Set';
                if (statusEl) statusEl.textContent = info.status === 'student' ? 'Student' : 'Graduate';
                if (locationEl) locationEl.textContent = info.location || 'Not Set';
                if (infoSection) infoSection.style.display = 'block';
            }

            // load tags
            // TBD
        }
    }
});



