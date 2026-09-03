# Менеджер задач (Java)

[![hexlet-check](https://github.com/mikitasazan/spring-boot-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/mikitasazan/spring-boot-project-99/actions)
[![Java CI](https://github.com/mikitasazan/spring-boot-project-99/actions/workflows/build.yml/badge.svg)](https://github.com/mikitasazan/spring-boot-project-99/actions)

Task Manager — система управления задачами (аналог Redmine): регистрация,
аутентификация, задачи, статусы, метки, фильтрация.

Учебный проект Хекслета: https://ru.hexlet.io/programs/spring-boot
Как это должно работать: https://files.hexlet.app/a/xg6yxv

## Стек

- Java 21, Spring Boot 4
- Gradle

## Установка

```bash
git clone https://github.com/mikitasazan/spring-boot-project-99.git
cd spring-boot-project-99
make setup
```

## Использование

```bash
make start
```

Приложение поднимется на `http://localhost:8080`. Проверка:

```bash
curl http://localhost:8080/welcome
# Welcome to Spring
```

Публичного деплоя нет — по решению владельца проект остаётся локальным, шаги
и автопроверка Hexlet не завязаны на живой стенд.

---

<details>
<summary>Автоматические тесты Хекслета</summary>

Тесты запускаются на каждый коммит. За запуск отвечает файл `.github/workflows/hexlet-check.yml` — не удаляйте и не переименовывайте ни его, ни репозиторий.

</details>

## О Хекслете

[Хекслет](https://ru.hexlet.io/) — школа программирования: авторские программы обучения с практикой, поддержкой наставников и реальными проектами, которые остаются в резюме. Этот репозиторий — один из таких проектов.
