# Day 16 MCP STDIO client

Минимальное консольное Spring Boot-приложение запускает `@modelcontextprotocol/server-everything@2025.11.25` через `npx`, выполняет MCP `initialize`, получает `tools/list`, выводит сведения о сервере и инструментах и закрывает клиент вместе с дочерним процессом. Инструменты обнаруживаются, но не вызываются.

## Версии

- JDK 21
- Spring Boot 4.1.1
- Kotlin 2.3.21
- MCP Java SDK 2.0.1
- Gradle Wrapper 9.7.1
- `@modelcontextprotocol/server-everything` 2025.11.25

## Требования

Нужны JDK 21, Node.js 20 или новее, `npx`, доступ к Maven Central и npm registry. Проверка окружения:

```bash
java -version
node --version
npx --version
```

Глобальная установка MCP-сервера не нужна. Первый запуск может занять больше времени, пока `npx` загружает npm-пакет.

## Сборка, тесты и запуск

```bash
./gradlew clean test
./gradlew integrationTest
./gradlew bootRun
./gradlew bootJar
java -jar build/libs/*.jar
```

`test` запускает быстрые unit-тесты. `integrationTest` запускает настоящий сервер через STDIO и проверяет production-путь `initialize` → `tools/list`.

Ожидаемый фрагмент лога:

```text
Connecting to MCP server via STDIO: npx
MCP initialized: protocol=..., server=... (...)
MCP tools received: count=...
MCP client closed
MCP tools available: count=...
MCP tool: name=echo, description=...
```

Команда настраивается свойством `app.mcp.command`; переменная `MCP_COMMAND` переопределяет значение из `application.yaml`. Например, в PowerShell на Windows:

```powershell
$env:MCP_COMMAND = "npx.cmd"
./gradlew.bat bootRun
```

Аргументы процесса и тайм-аут задаются через `app.mcp.arguments` и `app.mcp.request-timeout` в `application.yaml`.
