# Último Andar - contratos de locação

Aplicação para organizar imóveis, locadores, locatários e contratos de locação. Ela permite cadastrar os dados principais, gerar contratos, emitir aditivos, criar distratos e manter os documentos em PDF junto com o histórico do sistema.

O projeto foi pensado para rodar localmente durante o desenvolvimento e também pode ser levado para produção com Docker e PostgreSQL.

A ideia surgiu pelo meu background, automatizando uma tarefa que eu fazia em quantidade e levava um tempo considerável em meu emprego anterior. 

## Estrutura do projeto

- `backend`: API em Java 17 com Spring Boot, JPA, Flyway, H2 local e suporte a PostgreSQL.
- `frontend`: interface em React, TypeScript e Vite.
- `backend/data/db`: banco H2 usado no modo local.
- `backend/data/storage`: PDFs e fotos salvos pela aplicação. Os novos arquivos são criptografados em repouso.
- `scripts`: atalhos para rodar, auditar, fazer backup e restaurar o ambiente local.

## Rodando localmente

Antes de começar, confirme que você tem:

- Java 17.
- Maven 3.9 ou superior.
- Node.js 20 ou superior.

O caminho mais simples é usar o script local:

```powershell
.\scripts\run-local.ps1
```

Esse script sobe backend e frontend em janelas separadas. Se a porta `8080` já estiver ocupada, ele escolhe a próxima porta livre para o backend e ajusta o proxy do Vite automaticamente. Use as URLs que aparecerem no terminal.

Em uma instalação padrão, os endereços são:

- Frontend: http://localhost:5173
- API: http://localhost:8080
- Console H2 local: http://localhost:8080/h2-console

Primeiro acesso:

- Usuário: `admin`
- Senha: `admin123`

No primeiro login, o sistema vai pedir a troca da senha. Isso é esperado.

## Rodando manualmente

Backend:

```powershell
cd backend
mvn -q -DskipTests package
java -jar target\ultimo-andar-api-0.1.0.jar
```

Frontend:

```powershell
cd frontend
npm install
npm run dev
```

## Rodando com Docker

Crie o arquivo `.env` a partir do exemplo:

```powershell
Copy-Item .env.example .env
```

Antes de subir o ambiente, abra o `.env` e troque senhas, segredo JWT e chave de criptografia. Depois rode:

```powershell
docker compose up --build
```

Com a configuração padrão, os acessos ficam assim:

- Aplicação: http://localhost:8081
- API: http://localhost:8080
- PostgreSQL: localhost:5432

Se o Docker reclamar que não consegue conectar ao daemon, abra o Docker Desktop e espere ele terminar de iniciar. Depois rode o comando novamente.

## Segurança e dados pessoais

O projeto já inclui alguns cuidados importantes para trabalhar com dados pessoais:

- Login com token Bearer e expiração configurável.
- Troca obrigatória da senha inicial.
- Troca de senha pelo próprio usuário.
- Reset de senha feito por administrador, com senha temporária.
- Política de senha com no mínimo 10 caracteres, letra maiúscula, letra minúscula, número e símbolo.
- Bloqueio temporário após tentativas erradas.
- Limite de tentativas de login por IP.
- Perfis `ADMIN`, `OPERATOR` e `READER`.
- Auditoria com usuário, ação, recurso, detalhes e IP.
- Retenção automática dos eventos de auditoria.
- CPF e CNPJ validados por formato e dígitos verificadores, sem consulta à Receita Federal.
- CEP consultado pelo ViaCEP no frontend e normalizado no backend.
- Campos sensíveis criptografados no banco.
- PDFs e fotos criptografados no storage local.
- Upload de fotos limitado a JPG, PNG ou WEBP, com tamanho máximo de 8 MB.
- Console H2 desativado no Docker.
- Suporte a HTTPS via variáveis `SERVER_SSL_*`.
- Suporte a segredos por arquivo com `APP_SECURITY_JWT_SECRET_FILE` e `APP_CRYPTO_KEY_FILE`.

Antes de usar dados reais, troque obrigatoriamente `APP_SECURITY_JWT_SECRET` e `APP_CRYPTO_KEY`. Guarde essa chave de criptografia com cuidado: se ela mudar depois que houver dados criptografados, esses dados antigos não poderão ser lidos com a nova chave.

## Variáveis principais

- `APP_SECURITY_JWT_SECRET`: segredo usado para assinar os tokens de login.
- `APP_CRYPTO_KEY`: chave usada para criptografar campos sensíveis e arquivos.
- `APP_SECURITY_TOKEN_MINUTES`: tempo de validade do token.
- `APP_SECURITY_MAX_LOGIN_ATTEMPTS`: quantidade de tentativas erradas antes do bloqueio.
- `APP_SECURITY_LOCK_MINUTES`: duração do bloqueio.
- `APP_SECURITY_LOGIN_RATE_LIMIT_PER_MINUTE`: limite de tentativas de login por IP.
- `APP_RETENTION_AUDIT_DAYS`: quantidade de dias para manter eventos de auditoria.
- `APP_H2_CONSOLE_ENABLED`: liga ou desliga o console H2.

## Backup local

Para gerar um backup criptografado:

```powershell
.\scripts\backup-local.ps1
```

Para restaurar um backup:

```powershell
.\scripts\restore-backup.ps1 -BackupFile .\backups\nome-do-backup.zip.aes -Force
```

O backup inclui `backend/data`, ou seja, o banco H2 local e os arquivos salvos no storage.

## Auditoria de dependências

Use o script abaixo para revisar dependências do frontend e do backend:

```powershell
.\scripts\security-audit.ps1
```

Ele roda `npm audit` no frontend e OWASP Dependency Check no backend.

## Principais rotas da API

- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/change-password`
- `POST /api/users/{id}/reset-password`
- `GET /api/dashboard`
- `GET/POST/PUT/DELETE /api/owners`
- `GET/POST/PUT/DELETE /api/tenants`
- `GET/POST/PUT/DELETE /api/properties`
- `POST /api/properties/{id}/photos`
- `GET /api/contracts`
- `POST /api/contracts/generate`
- `POST /api/contracts/{id}/reissue`
- `POST /api/addendums/generate`
- `POST /api/terminations/generate`
- `GET /api/documents`
- `GET /api/documents/{id}/download`
- `GET/POST /api/templates`

## Azure
1. Azure Database for PostgreSQL no lugar do H2
2. Azure Blob Storage ou volume criptografado para documentos
3. Bbackend no Azure App Service ou Azure Container Apps e Frontend como Azure Static Web App ou container Nginx
4. Guardar senhas e chaves no Azure Key Vault
5. Ativar HTTPS
6. Configurar backup gerenciado
7. Configurar monitoramento de logs e alertas
8. dominio
Produção sem segredos diretamente no repositório
Usar variáveis de ambiente, arquivos de segredo ou o Key Vault.

Sempre em atualização e evolução.

 © Lucas Melquiades de Santana Martins
 