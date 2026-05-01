# K6 load tests

Este diretório contém scripts de teste de performance em K6 para o workspace.

## Script disponível

- `random-names.js`: faz requisições `GET /api/random-names` e valida:
  - status HTTP `200`
  - header `content-type` iniciando com `application/json`
  - `resource = "random-names"`
  - `status = "ready"`
  - `total = 5`
  - pelo menos um item no array `names`

## Pré-requisitos

Antes de executar o script:

1. tenha o `k6` instalado na sua máquina
2. suba a aplicação Quarkus que expõe o endpoint `/api/random-names`

Exemplo para subir a aplicação no projeto `quarkus-playground-updstream`:

```zsh
cd /Users/robot/Git/personal/quarkus-workspace/quarkus-playground-updstream
./mvnw quarkus:dev
```

Se você estiver sem Docker/Testcontainers e precisar desabilitar Dev Services, pode subir com a configuração adequada no projeto antes de rodar o K6.

## Como rodar o script

A partir da raiz do workspace:

```zsh
k6 run /Users/robot/Git/personal/quarkus-workspace/k6/random-names.js
```

Ou entrando na pasta `k6`:

```zsh
cd /Users/robot/Git/personal/quarkus-workspace/k6
k6 run random-names.js
```

## Parâmetros aceitos na execução

O script lê parâmetros via variáveis de ambiente.

### 1. URL base da aplicação

Pode ser informada de duas formas:

- `K6_BASE_URL`
- `BASE_URL`

Valor default:

```text
http://localhost:8080
```

Exemplo:

```zsh
K6_BASE_URL=http://localhost:8080 k6 run /Users/robot/Git/personal/quarkus-workspace/k6/random-names.js
```

---

### 2. Quantidade de usuários virtuais

Pode ser informada de duas formas:

- `K6_USERS`
- `USERS`

Valor default:

```text
10
```

Exemplo:

```zsh
K6_USERS=20 k6 run /Users/robot/Git/personal/quarkus-workspace/k6/random-names.js
```

---

### 3. Tempo de rampa de subida

Pode ser informado de duas formas:

- `K6_RAMP_SECONDS`
- `RAMP_SECONDS`

Valor default:

```text
5
```

Exemplo:

```zsh
K6_RAMP_SECONDS=10 k6 run /Users/robot/Git/personal/quarkus-workspace/k6/random-names.js
```

---

### 4. Quantidade de repetições por usuário virtual

Pode ser informada de duas formas:

- `K6_REPEAT_COUNT`
- `REPEAT_COUNT`

Valor default:

```text
3
```

Exemplo:

```zsh
K6_REPEAT_COUNT=5 k6 run /Users/robot/Git/personal/quarkus-workspace/k6/random-names.js
```

---

### 5. Tempo de sustentação da carga

Parâmetro:

- `K6_HOLD_SECONDS`

Valor default calculado:

```text
max(5, REPEAT_COUNT)
```

Ou seja:

- se `REPEAT_COUNT` for menor que `5`, o hold será `5`
- se `REPEAT_COUNT` for maior que `5`, o hold será o mesmo valor de `REPEAT_COUNT`

Exemplo:

```zsh
K6_HOLD_SECONDS=15 k6 run /Users/robot/Git/personal/quarkus-workspace/k6/random-names.js
```

---

### 6. Tempo de rampa de descida

Parâmetro:

- `K6_RAMP_DOWN_SECONDS`

Valor default:

```text
1
```

Exemplo:

```zsh
K6_RAMP_DOWN_SECONDS=3 k6 run /Users/robot/Git/personal/quarkus-workspace/k6/random-names.js
```

## Exemplo completo

```zsh
K6_BASE_URL=http://localhost:8080 \
K6_USERS=20 \
K6_RAMP_SECONDS=10 \
K6_REPEAT_COUNT=5 \
K6_HOLD_SECONDS=15 \
K6_RAMP_DOWN_SECONDS=3 \
k6 run /Users/robot/Git/personal/quarkus-workspace/k6/random-names.js
```

## Comportamento do cenário

O script usa o executor `ramping-vus` do K6 com este comportamento:

1. sobe de `0` até `USERS` em `RAMP_SECONDS`
2. mantém `USERS` por `HOLD_SECONDS`
3. reduz de `USERS` para `0` em `K6_RAMP_DOWN_SECONDS`

Dentro de cada iteração do cenário, cada usuário faz `REPEAT_COUNT` chamadas para:

```text
GET /api/random-names
```

## Observações

- O script foi criado para espelhar o cenário do Gatling existente no workspace.
- As variáveis sem prefixo, como `BASE_URL`, `USERS`, `RAMP_SECONDS` e `REPEAT_COUNT`, continuam aceitas como fallback.
- Se a aplicação não estiver disponível na URL informada, o teste irá falhar nas validações e nas métricas de erro HTTP.

