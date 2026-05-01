# Performance Testing Skill

Automation completa para testes de performance da aplicação Quarkus.

## 🚀 Funcionalidades

### Pipeline Completo
- ✅ Build automático da aplicação Quarkus
- ✅ Start/stop de containers Docker com limites de recursos
- ✅ Execução de testes K6 em múltiplos níveis
- ✅ Geração automática de relatórios
- ✅ Validação e tratamento de erros

### Tipos de Teste
1. **Basic** - 3 minutos, 10 VUs (baseline)
2. **Progressive** - 15 minutos, até 200 VUs (carga progressiva)
3. **Degradation** - 6.5 minutos, até 1000 VUs (encontrar limite)
4. **Extreme** - 6.5 minutos, até 2000 VUs (limite absoluto)

## 📋 Pré-requisitos

- Docker (rodando)
- k6 (instalado e no PATH)
- Maven (instalado)
- Projeto Quarkus na estrutura esperada

## 🎯 Como Usar

### Execução Interativa
```bash
./performance-skill.sh
```

### Menu de Opções
```
=== PERFORMANCE TESTING MENU ===
1) Full Pipeline (Build + Test + Report)
2) Build Application Only
3) Start Containers Only
4) Run Basic Test (3 min, 10 VUs)
5) Run Progressive Test (15 min, up to 200 VUs)
6) Run Degradation Test (6.5 min, up to 1000 VUs)
7) Run Extreme Test (6.5 min, up to 2000 VUs)
8) Run All Tests
9) Generate Reports Only
10) Cleanup (Stop Containers)
0) Exit
```

## 📊 Resultados Esperados

### Performance Máxima Conhecida
| Teste | VUs | TPS | P95 | Status |
|-------|-----|-----|-----|--------|
| Basic | 10 | 8.37 | 4.97ms | ✅ Estável |
| Progressive | 200 | 791.78 | 6.7ms | ✅ Estável |
| Degradation | 1000 | 16,809 | 62.62ms | ✅ Estável |
| Extreme | 2000 | 17,474 | 235.03ms | ⚠️ Degradação |

### Ponto de Degradação
- **Seguro**: Até 1000 VUs (16,809 TPS)
- **Máximo**: Até 2000 VUs (17,474 TPS com degradação)
- **Limite crítico**: Entre 1000-1500 VUs

## 📁 Arquivos Gerados

### Relatórios
- `reports_TIMESTAMP/performance_summary_TIMESTAMP.md` - Resumo completo
- `results_TIMESTAMP.json` - Dados brutos do teste básico
- `progressive_TIMESTAMP.json` - Dados do teste progressivo
- `degradation_TIMESTAMP.json` - Dados do teste de degradação
- `extreme_TIMESTAMP.json` - Dados do teste extremo

### Logs
- `performance-skill.log` - Log completo da execução

## 🔧 Configuração

### Docker Compose
O container é configurado com:
- **CPU**: 1 vCPU (limite)
- **Memória**: 1GB (limite)
- **Porta**: 8081 (mapeada para 8080 interna)

### Testes K6
Todos os testes usam:
- **Endpoint**: `http://localhost:8081/api/random-names`
- **Método**: GET
- **Validações**: Status 200, response time, conteúdo

## 🚨 Tratamento de Erros

O script inclui validação automática de:
- Docker rodando
- k6 disponível
- Maven instalado
- Estrutura do projeto
- Build da aplicação
- Start dos containers
- Resposta da aplicação

## 📈 Exemplo de Uso

### Pipeline Completo
```bash
./performance-skill.sh
# Opção 1 - Full Pipeline
```

### Teste Individual
```bash
./performance-skill.sh
# Opção 6 - Run Degradation Test
```

### Apenas Relatórios
```bash
./performance-skill.sh
# Opção 9 - Generate Reports Only
```

## 🎯 Recomendações de Uso

### Para Desenvolvimento
- Use **Basic Test** para validações rápidas
- Use **Progressive Test** para testes de regressão

### Para Produção
- Use **Degradation Test** para encontrar limites
- Monitore P95 < 100ms para operação segura

### Para Capacity Planning
- Use **Extreme Test** para encontrar limite absoluto
- Baseie decisões em 16,809 TPS (limite seguro)

## 🔍 Monitoramento

Durante os testes, o script monitora:
- Status dos containers
- Response times
- Taxa de erros
- Utilização de recursos
- Logs da aplicação

## 📞 Suporte

Em caso de erros:
1. Verifique o log: `performance-skill.log`
2. Confirme pré-requisitos
3. Verifique se Docker está rodando
4. Valide estrutura do projeto

---
**Desenvolvido por**: AI Assistant  
**Versão**: 1.0  
**Última atualização**: 2026-05-01
