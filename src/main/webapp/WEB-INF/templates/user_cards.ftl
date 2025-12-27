<#if users?? && users?size > 0>
    <#list users as user>
        <div class="user-card" data-gender="${user.gender!''}" data-country="${user.country!''}">
            <div class="card-header">
                <img src="${user.avatar!''}" alt="Avatar" class="user-avatar">
                <h3>${user.fullName!''}</h3>
            </div>
            <div class="card-body">
                <p><strong>Email:</strong> ${user.email!''}</p>
                <p><strong>Телефон:</strong> ${user.phone!''}</p>
                <p><strong>Адрес:</strong> ${user.address!''}</p>
                <p><strong>Дата рождения:</strong> ${user.birthDate!''}</p>
                <p><strong>Возраст:</strong> ${user.age}</p>
                <p><strong>Пол:</strong> ${user.gender!''}</p>
                <p><strong>Страна:</strong> ${user.country!''}</p>
            </div>
        </div>
    </#list>
<#else>
    <p class="no-users">Пользователи не найдены</p>
</#if>



