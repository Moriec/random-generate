let allUsers = [];
let filteredUsers = [];
let currentPage = 1;
const itemsPerPage = 10;
let currentSort = { column: null, direction: 'asc' };

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('userForm');
    const filterGender = document.getElementById('filterGender');
    const filterNationality = document.getElementById('filterNationality');
    const applyFiltersBtn = document.getElementById('applyFilters');
    const resetFiltersBtn = document.getElementById('resetFilters');
    const exportCsvBtn = document.getElementById('exportCsv');
    const exportJsonBtn = document.getElementById('exportJson');

    form.addEventListener('submit', handleFormSubmit);
    applyFiltersBtn.addEventListener('click', applyFilters);
    resetFiltersBtn.addEventListener('click', resetFilters);
    exportCsvBtn.addEventListener('click', () => exportData('csv'));
    exportJsonBtn.addEventListener('click', () => exportData('json'));

    // Сортировка по колонкам
    document.querySelectorAll('.sortable').forEach(header => {
        header.addEventListener('click', () => {
            const column = header.dataset.sort;
            sortUsers(column);
        });
    });
});

async function handleFormSubmit(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const count = formData.get('count');
    const gender = formData.get('gender');
    const nationality = formData.get('nationality');

    showLoading();

    try {
        const params = new URLSearchParams({
            action: 'generate',
            count: count,
            gender: gender,
            nationality: nationality
        });

        const contextPath = window.contextPath || '';
        const response = await fetch(`${contextPath}/users?${params.toString()}`, {
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Ошибка ответа сервера:', response.status, errorText);
            throw new Error(`Ошибка при загрузке данных: ${response.status} - ${errorText}`);
        }

        const responseData = await response.json();
        console.log('Получены данные от сервера:', responseData);
        
        if (!Array.isArray(responseData)) {
            console.error('Ответ не является массивом:', responseData);
            throw new Error('Неверный формат данных от сервера');
        }
        
        allUsers = responseData;
        filteredUsers = [...allUsers];
        currentPage = 1;
        currentSort = { column: null, direction: 'asc' };

        displayUsers();
        hideLoading();
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Произошла ошибка при генерации пользователей');
        hideLoading();
    }
}

function applyFilters() {
    const genderFilter = document.getElementById('filterGender').value;
    const nationalityFilter = document.getElementById('filterNationality').value.toLowerCase().trim();

    filteredUsers = allUsers.filter(user => {
        const genderMatch = genderFilter === 'all' || user.gender === genderFilter;
        const nationalityMatch = nationalityFilter === '' || 
            (user.country && user.country.toLowerCase().includes(nationalityFilter));
        return genderMatch && nationalityMatch;
    });

    currentPage = 1;
    displayUsers();
}

function resetFilters() {
    document.getElementById('filterGender').value = 'all';
    document.getElementById('filterNationality').value = '';
    filteredUsers = [...allUsers];
    currentPage = 1;
    displayUsers();
}

function sortUsers(column) {
    if (currentSort.column === column) {
        currentSort.direction = currentSort.direction === 'asc' ? 'desc' : 'asc';
    } else {
        currentSort.column = column;
        currentSort.direction = 'asc';
    }

    filteredUsers.sort((a, b) => {
        let aVal, bVal;

        switch (column) {
            case 'name':
                aVal = a.fullName || '';
                bVal = b.fullName || '';
                break;
            case 'age':
                aVal = a.age || 0;
                bVal = b.age || 0;
                break;
            case 'country':
                aVal = a.country || '';
                bVal = b.country || '';
                break;
            default:
                return 0;
        }

        if (typeof aVal === 'string') {
            aVal = aVal.toLowerCase();
            bVal = bVal.toLowerCase();
        }

        if (aVal < bVal) return currentSort.direction === 'asc' ? -1 : 1;
        if (aVal > bVal) return currentSort.direction === 'asc' ? 1 : -1;
        return 0;
    });

    updateSortIndicators();
    displayUsers();
}

function updateSortIndicators() {
    document.querySelectorAll('.sortable').forEach(header => {
        header.classList.remove('sort-asc', 'sort-desc');
        if (header.dataset.sort === currentSort.column) {
            header.classList.add(`sort-${currentSort.direction}`);
        }
    });
}

function displayUsers() {
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageUsers = filteredUsers.slice(startIndex, endIndex);

    displayTable(pageUsers);
    displayPagination();
    
    document.getElementById('userSection').style.display = 'block';
}

function displayTable(users) {
    const tbody = document.getElementById('usersTableBody');
    tbody.innerHTML = '';

    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${escapeHtml(user.fullName || '')}</td>
            <td>${user.age || 0}</td>
            <td>${escapeHtml(user.country || '')}</td>
            <td>${escapeHtml(user.email || '')}</td>
            <td>${escapeHtml(user.phone || '')}</td>
            <td>${escapeHtml(user.gender || '')}</td>
            <td>${escapeHtml(user.birthDate || '')}</td>
        `;
        tbody.appendChild(row);
    });
}

function displayPagination() {
    const totalPages = Math.ceil(filteredUsers.length / itemsPerPage);
    const paginationInfo = document.getElementById('paginationInfo');
    const paginationButtons = document.getElementById('paginationButtons');

    const startItem = (currentPage - 1) * itemsPerPage + 1;
    const endItem = Math.min(currentPage * itemsPerPage, filteredUsers.length);
    
    paginationInfo.textContent = `Показано ${startItem}-${endItem} из ${filteredUsers.length} пользователей`;

    paginationButtons.innerHTML = '';

    if (totalPages <= 1) {
        return;
    }

    // Кнопка "Предыдущая"
    const prevButton = document.createElement('button');
    prevButton.textContent = '← Предыдущая';
    prevButton.disabled = currentPage === 1;
    prevButton.addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            displayUsers();
        }
    });
    paginationButtons.appendChild(prevButton);

    // Номера страниц
    const maxVisiblePages = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    if (startPage > 1) {
        const firstButton = document.createElement('button');
        firstButton.textContent = '1';
        firstButton.addEventListener('click', () => {
            currentPage = 1;
            displayUsers();
        });
        paginationButtons.appendChild(firstButton);

        if (startPage > 2) {
            const ellipsis = document.createElement('span');
            ellipsis.textContent = '...';
            ellipsis.style.padding = '0 10px';
            paginationButtons.appendChild(ellipsis);
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        const pageButton = document.createElement('button');
        pageButton.textContent = i;
        if (i === currentPage) {
            pageButton.classList.add('active');
        }
        pageButton.addEventListener('click', () => {
            currentPage = i;
            displayUsers();
        });
        paginationButtons.appendChild(pageButton);
    }

    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            const ellipsis = document.createElement('span');
            ellipsis.textContent = '...';
            ellipsis.style.padding = '0 10px';
            paginationButtons.appendChild(ellipsis);
        }

        const lastButton = document.createElement('button');
        lastButton.textContent = totalPages;
        lastButton.addEventListener('click', () => {
            currentPage = totalPages;
            displayUsers();
        });
        paginationButtons.appendChild(lastButton);
    }

    // Кнопка "Следующая"
    const nextButton = document.createElement('button');
    nextButton.textContent = 'Следующая →';
    nextButton.disabled = currentPage === totalPages;
    nextButton.addEventListener('click', () => {
        if (currentPage < totalPages) {
            currentPage++;
            displayUsers();
        }
    });
    paginationButtons.appendChild(nextButton);
}

function exportData(format) {
    if (filteredUsers.length === 0) {
        alert('Нет данных для экспорта');
        return;
    }

    const contextPath = window.contextPath || '';
    window.location.href = `${contextPath}/export?format=${format}`;
}

function showLoading() {
    document.getElementById('loadingIndicator').style.display = 'block';
    document.getElementById('userSection').style.display = 'none';
}

function hideLoading() {
    document.getElementById('loadingIndicator').style.display = 'none';
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

