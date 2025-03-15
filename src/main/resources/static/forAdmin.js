document.addEventListener('DOMContentLoaded', function () {
    fetchLoggedUser();
    fetchUsers();
    loadRoles();
    setupCloseButtons();
});

// Функция для получения и отображения текущего пользователя
function fetchLoggedUser() {
    console.log('Fetching authorized user info...');
    fetch('/admin/loggedUser')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch authorized user info');
            }
            return response.json();
        })
        .then(user => {
            console.log('Authorized user fetched:', user);
            const emailSpan = document.getElementById('loggedUserEmail');
            const roleSpan = document.getElementById('loggedUserRole');
            emailSpan.textContent = user.email;
            roleSpan.textContent = user.roles.map(role => role.name.substring(5)).join(', ');
        })
        .catch(error => {
            console.error('Error fetching authorized user info:', error);
        });
}

// Функция для получения и отображения всех пользователей
function fetchUsers() {
    console.log('Fetching users for table...');
    fetch('/admin/users')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch users');
            }
            return response.json();
        })
        .then(response => {
            console.log('Users for table fetched:', response);
            const tableBody = document.getElementById('users-table-body');
            tableBody.innerHTML = ''; // Очищаем существующие строки
            response.forEach(user => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.lastname}</td>
                    <td>${user.age}</td>
                    <td>${user.email}</td>
                    <td>${user.roles.map(role => role.name.substring(5)).join(', ')}</td> 
                    <td><button class="btn btn-info" onclick="openEditUserModal(${user.id})">Edit</button></td>
                    <td><button class="btn btn-danger" onclick="openDeleteUserModal(${user.id})">Delete</button></td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Error fetching users for table:', error);
            alert('Ошибка при загрузке пользователей');
        });
}

// Функция для загрузки всех ролей и отображения их в селектах
function loadRoles() {
    console.log('Loading roles...');
    fetch('/admin/users/roles')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch roles');
            }
            return response.json();
        })
        .then(roles => {
            console.log('Roles fetched:', roles);
            const roleSelect = document.getElementById('roles');
            const editRoleSelect = document.getElementById('editRoles');
            roleSelect.innerHTML = '';
            editRoleSelect.innerHTML = '';
            roles.forEach(role => {
                const option = document.createElement('option');
                option.value = role.id;
                option.text = role.authority.substring(5);
                roleSelect.appendChild(option);
                const editOption = document.createElement('option');
                editOption.value = role.id;
                editOption.text = role.authority.substring(5);
                editRoleSelect.appendChild(editOption);
            });
        })
        .catch(error => {
            console.error('Error loading roles:', error);
            alert('Ошибка при загрузке ролей');
        });
}

// Обработчик отправки формы создания нового пользователя
document.getElementById('new-user-form').addEventListener('submit', function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    const rolesSelected = Array.from(document.getElementById('roles').selectedOptions).map(option => ({
        id: parseInt(option.value, 10)
    }));
    const user = {
        username: formData.get('firstName'),
        lastname: formData.get('lastName'),
        age: Number(formData.get('age')),
        email: formData.get('email'),
        password: formData.get('password'),
        roles: rolesSelected
    };
    console.log('Creating user:', user);
    fetch('/admin/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
        .then(response => {
            if (response.ok) {
                fetchUsers();
                alert('Пользователь успешно создан!');
                this.reset();
                // closeModal('newUserPopup');

                const usersTab = document.getElementById('show-users-table');
                if (usersTab) {
                    usersTab.click();
                }

            } else {
                return response.json().then(data => {
                    throw new Error(data.message || 'Не удалось создать пользователя');
                });
            }
        })
        .catch(error => {
            console.error('Error creating user:', error);
            alert('Ошибка при создании пользователя: ' + error.message);
        });
});

// Функция для открытия модального окна редактирования пользователя и заполнения формы
function openEditUserModal(id) {
    console.log('Opening edit modal for user ID:', id);
    fetch(`/admin/users/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch user');
            }
            return response.json();
        })
        .then(user => {
            console.log('User fetched for edit:', user);
            document.getElementById('editUserId').value = user.id;
            document.getElementById('editFirstName').value = user.username;
            document.getElementById('editLastName').value = user.lastname;
            document.getElementById('editAge').value = user.age;
            document.getElementById('editEmail').value = user.email;
            const editRolesSelect = document.getElementById('editRoles');
            Array.from(editRolesSelect.options).forEach(option => {
                option.selected = user.roles.some(role => role.id === parseInt(option.value, 10));
            });
            $('#editUserModal').modal('show');
        })
        .catch(error => {
            console.error('Error fetching user:', error);
            alert('Ошибка при загрузке данных пользователя');
        });
}

// Обработчик отправки формы редактирования пользователя
document.getElementById('editUserForm').addEventListener('submit', function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    const id = Number(formData.get('editUserId'));
    const rolesSelected = Array.from(document.getElementById('editRoles').selectedOptions).map(option => ({
        id: parseInt(option.value, 10)
    }));
    const user = {
        id: id,
        username: formData.get('editFirstName'),
        lastname: formData.get('editLastName'),
        age: Number(formData.get('editAge')),
        email: formData.get('editEmail'),
        password: formData.get('editPassword'),
        roles: rolesSelected
    };
    console.log('Updating user:', user);
    fetch(`/admin/users/${id}`, {
        // method: 'PUT',
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
        .then(response => {
            if (response.ok) {
                fetchUsers(); // Перезагрузка таблицы
                alert('Пользователь успешно обновлен!');
                $('#overlay').modal('hide');
                $('#editUserModal').modal('hide');
            } else {
                return response.json().then(data => {
                    console.error('Ошибка обновления:', data);
                    alert('Ошибка при обновлении пользователя: ' + data.message);
                });
            }
        })
        .catch(error => {
            console.error('Error updating user:', error);
            alert('Ошибка при обновлении пользователя: ' + error.message);
        });
});


// Функция для открытия модального окна удаления пользователя
function openDeleteUserModal(id) {
    console.log('Opening delete modal for user ID:', id);
    fetch(`/admin/users/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch user');
            }
            return response.json();
        })
        .then(user => {
            console.log('User fetched for delete:', user);
            document.getElementById('deleteUserId').value = user.id;
            document.getElementById('deleteUsername').value = user.username;
            document.getElementById('deleteSurname').value = user.lastname;
            document.getElementById('deleteAge').value = user.age;
            document.getElementById('deleteEmail').value = user.email;
            const deleteRolesSelect = document.getElementById('deleteRoles');
            deleteRolesSelect.innerHTML = '';
            user.roles.forEach(role => {
                const option = document.createElement('option');
                option.value = role.id;
                option.text = role.name.substring(5);
                deleteRolesSelect.appendChild(option);
            });
            $('#deleteUserModal').modal('show'); //
        })
        .catch(error => {
            console.error('Error fetching user:', error);
            alert('Ошибка при загрузке данных пользователя');
        });
}

// Обработчик формы удаления пользователя
document.getElementById('deleteUserForm').addEventListener('submit', function (event) {
    event.preventDefault();
    const id = document.getElementById('deleteUserId').value;
    console.log('Submitting delete form for user ID:', id);
    fetch(`/admin/users/${id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                fetchUsers();
                alert('Пользователь успешно удалён!');
                $('#deleteUserModal').modal('hide'); // Закрываем модальное окно после удаления
            } else {
                return response.json().then(data => {
                    throw new Error(data.message || 'Не удалось удалить пользователя');
                });
            }
        })
        .catch(error => {
            console.error('Error deleting user:', error);
            alert('Ошибка при удалении пользователя: ' + error.message);
        });
});

// Закрытие модальных окон при клике на оверлей
function setupCloseButtons() {
    const overlay = document.getElementById('overlay');
    overlay.addEventListener('click', function () {
        const modals = document.querySelectorAll('.popup');
        modals.forEach(modal => {
            modal.style.display = 'none';
        });
        this.style.display = 'none';
        document.body.style.overflow = 'auto';
    });
}