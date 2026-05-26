# Лабораторная работа №4: Безопасность, обеспечение качества и непрерывная доставка

## Обзор

Данная лабораторная работа направлена на внедрение лучших практик безопасности, сканирование качества кода и автоматизацию непрерывной доставки в DevOps-конвейере.

## Цели

1. Изучить лучшие практики обеспечения безопасности приложений и инфраструктуры в DevOps
2. Настроить автоматическое сканирование кода на уязвимости с помощью SonarQube
3. Обеспечить завершение CI с ошибкой, если качество не соответствует требованиям (покрытие < 80%, баги и т.д.)
4. Автоматизировать развертывание приложения с помощью Argo CD
5. Интегрировать Telegram-бот для отправки статуса jobs в pipeline CI/CD

## Настройка Telegram-бота

### Шаг 1: Создание Telegram-бота

1. Откройте Telegram и найдите **@BotFather**
2. Отправьте команду: `/newbot`
3. Следуйте инструкциям:
   - Введите название бота (например, `DevOps CI Bot`)
   - Введите username бота (должен заканчиваться на `bot`, например, `devops_ci_bot`)
4. BotFather выдаст токен вида: `123456789:ABCdefGHIjklMNOpqrsTUVwxyz`
5. **Сохраните этот токен** - он понадобится для GitHub secrets

### Шаг 2: Получение Chat ID

1. Откройте чат с созданным ботом, нажав ссылку от BotFather
2. Отправьте боту любое сообщение (например, "Привет")
3. Откройте в браузере URL (замените `YOUR_BOT_TOKEN`):
   ```
   https://api.telegram.org/YOUR_BOT_TOKEN/getUpdates
   ```
4. Найдите в JSON `"chat":{"id":123456789,...}`
5. **Сохраните этот chat ID** - он понадобится для GitHub secrets

### Шаг 3: Настройка GitHub Secrets

1. Перейдите в репозиторий на GitHub
2. Откройте **Settings → Secrets and variables → Actions**
3. Добавьте следующие секреты:

| Название секрета | Значение |
|------------------|----------|
| `TELEGRAM_BOT_TOKEN` | Токен от BotFather (например, `123456789:ABCdefGHIjklMNOpqrsTUVwxyz`) |
| `TELEGRAM_CHAT_ID` | Ваш chat ID (например, `123456789`) |

### Шаг 4: Тестирование бота

После настройки секретов конвейер автоматически отправляет уведомления. Также можно протестировать вручную:

```bash
# Отправка тестового сообщения через curl
curl -X POST "https://api.telegram.org/YOUR_BOT_TOKEN/sendMessage" \
  -H "Content-Type: application/json" \
  -d '{"chat_id": "YOUR_CHAT_ID", "text": "Тестовое сообщение от DevOps Bot"}'
```

## Настройка SonarQube

### Шаг 1: Регистрация в SonarCloud

1. Перейдите на [sonarcloud.io](https://sonarcloud.io)
2. Войдите через аккаунт GitHub
3. Авторизуйте приложение SonarCloud GitHub App

### Шаг 2: Создание проекта

1. Нажмите **+** → **Analyze new project**
2. Выберите ваш репозиторий
3. Выберите бесплатный план (автоматический анализ)

### Шаг 3: Получение ключа проекта и токена

1. Перейдите в **Administration → Security → Users**
2. Откройте вкладку **Tokens**
3. Создайте новый токен и **сохраните его**
4. Ключ проекта указан в настройках проекта

### Шаг 4: Настройка GitHub Secrets

| Название секрета | Значение |
|------------------|----------|
| `SONAR_TOKEN` | Токен от SonarCloud |
| `SONAR_PROJECT_KEY` | Ключ проекта (например, `ваш-username_devops_project`) |

## Настройка Argo CD

### Шаг 1: Установка Argo CD в Kubernetes

```bash
# Создание namespace
kubectl create namespace argocd

# Установка Argo CD
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# Ожидание готовности pods
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=argocd-server -n argocd --timeout=300s
```

### Шаг 2: Получение пароля администратора

```bash
# Получение пароля
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
```

### Шаг 3: Доступ к Argo CD UI

```bash
# Проброс порта для доступа к UI
kubectl port-forward svc/argocd-server -n argocd 8080:443

# Откройте в браузере: https://localhost:8080
# Username: admin
# Password: (из шага 2)
```

### Шаг 4: Настройка GitHub Secrets

| Название секрета | Значение |
|------------------|----------|
| `ARGOCD_AUTH_TOKEN` | Токен из Argo CD UI (Settings → User Info → Generate Token) |
| `ARGOCD_SERVER` | URL сервера Argo CD (например, `argocd.example.com`) |
| `ARGOCD_DOMAIN` | Домен для Argo CD ingress (например, `argocd.example.com`) |

## Запуск конвейера

### Ручной запуск

1. Перейдите на вкладку GitHub Actions
2. Выберите workflow **CI Build Test VM**
3. Нажмите **Run workflow** → выберите ветку → **Run workflow**

### Автоматический запуск

Конвейер запускается автоматически при:
- Push в ветку `main`
- Pull request в ветку `main`
- После завершения Terraform workflow

### Этапы конвейера

1. **SonarQube Scan** - Анализ качества и безопасности кода
2. **Backend Tests** - Java unit-тесты с проверкой покрытия 80%
3. **Frontend Tests** - JavaScript/React тесты
4. **Build & Push Images** - Docker образы в GHCR
5. **Kubernetes Deploy** - Развертывание в кластере
6. **Load Test** - Нагрузочное тестирование K6
7. **Telegram Notification** - Уведомление о статусе

## Детали реализации

### 1. Интеграция SonarQube

SonarQube интегрирован как отдельная job в CI-конвейер для статического анализа кода и проверки безопасности.

**Файлы:**
- [`.github/workflows/ci-build.yml`](.github/workflows/ci-build.yml) - Основной CI workflow с SonarQube job
- [`.github/workflows/sonar-qube.yml`](.github/workflows/sonar-qube.yml) - Выделенный SonarQube workflow

**Функции:**
- Сканирование backend-кода с Java-анализатором
- Сканирование frontend-кода с JavaScript/TypeScript анализатором
- Принудительное покрытие кода (минимум 80%)
- Проверка Quality Gate, блокирующая pipeline при ошибках
- OWASP проверка зависимостей
- Trivy сканирование уязвимостей

**Quality Gates:**
- Покрытие кода должно быть >= 80%
- Отсутствие критических багов
- Отсутствие security hotspots
- Все code smells должны быть исправлены

### 2. Argo CD GitOps Deployment

Argo CD настроен для непрерывной доставки с методологией GitOps.

**Файлы:**
- [`argocd/application.yaml`](argocd/application.yaml) - Определение Argo CD Application
- [`argocd/install.yaml`](argocd/install.yaml) - Манифесты установки Argo CD
- [`.github/workflows/argocd.yml`](.github/workflows/argocd.yml) - Workflow синхронизации Argo CD

**Функции:**
- Автоматическая синхронизация из Git-репозитория
- Self-healing возможности
- Pruning удаленных ресурсов
- Поддержка rollback
- Визуальная панель для статуса развертывания

### 3. Интеграция Telegram-бота

Telegram-бот отправляет уведомления о статусе CI/CD pipeline в реальном времени.

**Файлы:**
- [`src/main/java/com/devops/project/devops_project/service/TelegramNotificationService.java`](src/main/java/com/devops/project/devops_project/service/TelegramNotificationService.java) - Telegram сервис
- [`src/main/java/com/devops/project/devops_project/controller/TelegramBotController.java`](src/main/java/com/devops/project/devops_project/controller/TelegramBotController.java) - REST API endpoints
- [`src/main/resources/application.properties`](src/main/resources/application.properties) - Конфигурация Telegram

**Типы уведомлений:**
- Результаты сканирования SonarQube
- Статус jobs pipeline (успех/ошибка)
- Статус развертывания Argo CD
- Кастомные уведомления через REST API

## Требуемые секреты

Настройте следующие секреты в настройках GitHub репозитория:

| Название секрета | Описание |
|------------------|----------|
| `SONAR_TOKEN` | Токен аутентификации SonarCloud |
| `SONAR_PROJECT_KEY` | Ключ проекта SonarCloud |
| `TELEGRAM_BOT_TOKEN` | API токен Telegram бота |
| `TELEGRAM_CHAT_ID` | Chat ID для уведомлений |
| `ARGOCD_AUTH_TOKEN` | Токен аутентификации Argo CD |
| `ARGOCD_SERVER` | URL сервера Argo CD |
| `ARGOCD_DOMAIN` | Домен Argo CD для ingress |

## Поток конвейера

```
Push/PR → SonarQube Scan → Tests → Build → Push Images → Kubernetes Deploy → Load Test → Telegram Notification
                ↓
         Quality Gate Check
         (Coverage >= 80%)
                ↓
         Fail if not passed
```

## API Endpoints

### Telegram Bot Controller

| Endpoint | Метод | Описание |
|----------|-------|----------|
| `/api/telegram/notify` | POST | Отправить кастомное уведомление |
| `/api/telegram/pipeline-status` | POST | Отправить статус pipeline |
| `/api/telegram/deployment-status` | POST | Отправить статус развертывания |
| `/api/telegram/sonar-results` | POST | Отправить результаты SonarQube |

## Реализованные лучшие практики безопасности

1. **Static Application Security Testing (SAST)**
   - SonarQube анализ кода
   - OWASP Dependency Check
   - Trivy сканирование контейнеров

2. **Dynamic Application Security Testing (DAST)**
   - Интеграционные тесты с security assertions

3. **Безопасность инфраструктуры**
   - Kubernetes network policies
   - Управление секретами через Kubernetes secrets
   - Image pull secrets для приватных регистров

4. **Безопасность конвейера**
   - Шифрованное управление секретами
   - Ограничение прав доступа
   - Верификация артефактов

## Команды Argo CD

```bash
# Вход в Argo CD
argocd login <server> --username admin --password <password>

# Синхронизация приложения
argocd app sync devops-project

# Статус приложения
argocd app get devops-project

# История приложения
argocd app history devops-project

# Rollback к предыдущей версии
argocd app rollback devops-project <revision>
```

## Верификация

1. **SonarQube Quality Gate:**
   - Проверьте SonarCloud dashboard для статуса quality gate
   - Pipeline должен завершиться с ошибкой, если покрытие < 80%

2. **Argo CD Sync:**
   - Откройте Argo CD UI для проверки статуса синхронизации
   - Проверьте, что Kubernetes ресурсы корректно развернуты

3. **Telegram уведомления:**
   - Убедитесь, что бот получает уведомления после запуска pipeline
   - Проверьте формат и содержание сообщений

## Заключение

Данная лабораторная работа реализует комплексный DevOps-конвейер с:
- Сканированием безопасности на нескольких уровнях
- Quality gates, обеспечивающими стандарты кода
- GitOps-based непрерывной доставкой с Argo CD
- Уведомлениями в реальном времени через Telegram-бот

Все компоненты работают вместе для обеспечения качества кода, безопасности и надежных развертываний.
