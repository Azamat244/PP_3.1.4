document.addEventListener("DOMContentLoaded", function () {
    fetch("/profile_user")
        .then(response => response.json())
        .then(user => {
            console.log(user)
            document.getElementById("user-tab").textContent = user.username;
            document.getElementById("userId").textContent = user.id;
            document.getElementById("userEmail").textContent = user.email;
            document.getElementById("userName").textContent = user.username;
            document.getElementById("userLastName").textContent = user.lastname;
            document.getElementById("userAge").textContent = user.age;

            let roles = user.authorities.map(role => role.authority.substring(5)).join(", ");
            document.getElementById("userRoles").textContent = roles;
        })
        .catch(error => console.log("Ошибка загрузки пользователей: ", error))
})