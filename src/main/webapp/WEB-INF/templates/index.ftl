<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title} - Random User Generator</title>
    <link rel="stylesheet" href="${contextPath}/css/styles.css">
    <script>
        window.contextPath = "${contextPath}";
    </script>
</head>
<body>
    <div class="container">
        <header>
            <h1>Random User Generator</h1>
            <p>Генерация случайных пользователей с помощью Random User API</p>
        </header>

        <section class="form-section">
            <form id="userForm" class="generation-form">
                <div class="form-group">
                    <label for="count">Количество пользователей (1-100):</label>
                    <input type="number" id="count" name="count" min="1" max="100" value="10" required>
                </div>

                <div class="form-group">
                    <label for="gender">Пол:</label>
                    <select id="gender" name="gender">
                        <option value="all">Все</option>
                        <option value="male">Мужской</option>
                        <option value="female">Женский</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="nationality">Национальность (код страны, например: US, GB, DE):</label>
                    <input type="text" id="nationality" name="nationality" placeholder="Оставьте пустым для всех">
                </div>

                <button type="submit" class="btn-primary">Сгенерировать</button>
            </form>
        </section>

        <section class="controls-section">
            <div class="filters">
                <label for="filterGender">Фильтр по полу:</label>
                <select id="filterGender">
                    <option value="all">Все</option>
                    <option value="male">Мужской</option>
                    <option value="female">Женский</option>
                </select>

                <label for="filterNationality">Фильтр по стране:</label>
                <input type="text" id="filterNationality" placeholder="Введите страну">

                <button type="button" id="applyFilters" class="btn-secondary">Применить фильтры</button>
                <button type="button" id="resetFilters" class="btn-secondary">Сбросить</button>
            </div>

            <div class="export-buttons">
                <button type="button" id="exportCsv" class="btn-export">Экспорт CSV</button>
                <button type="button" id="exportJson" class="btn-export">Экспорт JSON</button>
            </div>
        </section>

        <section id="loadingIndicator" class="loading" style="display: none;">
            <div class="spinner"></div>
            <p>Загрузка пользователей...</p>
        </section>

        <section id="userSection" style="display: none;">
            <div class="table-controls">
                <div class="pagination-info">
                    <span id="paginationInfo"></span>
                </div>
                <div class="pagination-buttons" id="paginationButtons"></div>
            </div>

            <div class="table-container">
                <table id="usersTable" class="users-table">
                    <thead>
                        <tr>
                            <th class="sortable" data-sort="name">
                                Имя <span class="sort-arrow"></span>
                            </th>
                            <th class="sortable" data-sort="age">
                                Возраст <span class="sort-arrow"></span>
                            </th>
                            <th class="sortable" data-sort="country">
                                Страна <span class="sort-arrow"></span>
                            </th>
                            <th>Email</th>
                            <th>Телефон</th>
                            <th>Пол</th>
                            <th>Дата рождения</th>
                        </tr>
                    </thead>
                    <tbody id="usersTableBody">
                    </tbody>
                </table>
            </div>

            <div id="userCards" class="user-cards">
                <!-- User cards will be inserted here -->
            </div>
        </section>
    </div>

    <script src="${contextPath}/js/app.js"></script>
</body>
</html>

