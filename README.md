# Quarkus Performance Testing Workspace

Este workspace contém uma suite completa de testes de performance para serviços Quarkus, incluindo análises comparativas entre diferentes arquiteturas e recomendações para produção.

## 🏗️ Arquitetura do Workspace

```
quarkus-workspace/
├── downstream/                    # Serviço Quarkus puro (porta 8080)
├── upstream/                      # Serviço Quarkus upstream (porta 8081)
├── upstream-camel/                # Serviço Quarkus + Camel (porta 8082)
├── k6/                           # Suite de testes de performance
│   ├── scripts/                  # Scripts k6
│   ├── output/                   # Dados brutos dos testes
│   └── reports/                  # Relatórios de análise
└── docker-compose.yml           # Orquestração dos serviços
```

## 🚀 Serviços Disponíveis

### 1. Downstream Service (Porta 8080)
- **Arquitetura:** Quarkus puro
- **Performance:** 🏆 Excelente (P95: 58.62ms, TPS: 13,275)
- **Uso Recomendado:** APIs críticas, alta performance

### 2. Upstream Service (Porta 8081)
- **Arquitetura:** Quarkus + comunicação com downstream
- **Performance:** ✅ Bom (P95: 136.68ms, TPS: 6,238)
- **Uso Recomendado:** APIs gerais, integrações simples

### 3. Upstream-Camel Service (Porta 8082)
- **Arquitetura:** Quarkus + Apache Camel
- **Performance:** ⚠️ Aceitável (P95: 154.20ms, TPS: 5,281)
- **Uso Recomendado:** Integrações complexas, transformação de dados

## 🧪 Testes de Performance

### Framework Utilizado
- **Ferramenta:** k6
- **Tipo:** Load testing progressivo
- **Padrão:** 10→50→100→200→400→600→800→1000 VUs
- **Duração:** 4 minutos 30 segundos por teste

### Testes Executados

1. **Downstream Performance Test**
   - Total Requests: 3,584,226
   - Error Rate: 0.00%
   - Status: ✅ Production Ready

2. **Upstream vs Upstream-Camel Comparison**
   - Upstream: +18% throughput, -11% latência
   - Camel overhead: ~15-18% perda de performance

## 📊 Resultados Principais

| Serviço | P95 Response Time | TPS | Status | Recomendação |
|---------|-------------------|-----|--------|--------------|
| **Downstream** | 58.62ms | 13,275 | 🏆 Excelente | APIs críticas |
| **Upstream** | 136.68ms | 6,238 | ✅ Bom | APIs gerais |
| **Upstream-Camel** | 154.20ms | 5,281 | ⚠️ Aceitável | Integrações complexas |

## 🚀 Quick Start

### 1. Iniciar os Serviços
```bash
docker-compose up -d
```

### 2. Verificar Serviços
```bash
curl http://localhost:8080/api/random-names  # Downstream
curl http://localhost:8081/api/random-names  # Upstream
curl http://localhost:8082/api/random-names  # Upstream-Camel
```

### 3. Executar Testes de Performance
```bash
cd k6
k6 run scripts/k6-unified-test.js
```

## 📈 Relatórios de Performance

### Relatórios Disponíveis
- **[Análise Final](.k6/reports/final-performance-analysis-20260501-1740.md)** - Análise completa e conclusões
- **[Comparativo Upstream](.k6/reports/comparative-analysis-upstream-vs-upstream-camel-20260501-1730.md)** - Comparação detalhada upstream vs upstream-camel
- **[Performance Downstream](.k6/reports/unified-performance-report-20260501-1713.md)** - Análise detalhada do serviço downstream

### Dados Brutos
- **k6/output/** - Arquivos JSON com dados completos dos testes
- **k6/*.json** - Resultados mais recentes dos testes executados

## 🎯 Recomendações de Uso

### Para Performance Crítica
- Use **Downstream (8080)** para APIs de alto volume
- Zero overhead, latência mínima
- Ideal para endpoints críticos de negócio

### Para Integrações Simples
- Use **Upstream (8081)** para comunicação entre serviços
- Performance sólida com overhead mínimo
- Bom para APIs internas

### Para Integrações Complexas
- Use **Upstream-Camel (8082)** para transformação de dados
- Framework poderoso para integrações enterprise
- Aceitável quando flexibilidade é mais importante que performance

## 🔧 Configuração

### Recursos dos Containers
- **CPU:** 1 vCPU limit
- **Memory:** 1GB RAM limit
- **Platform:** Docker + Quarkus JVM

### Thresholds de Performance
- **P95 Response Time:** < 1000ms
- **Error Rate:** < 20%
- **P95 Violations:** < 5%

## 📋 Métricas de Escalabilidade

### Pontos de Scaling Recomendados
- **Downstream:** Escalar horizontalmente a partir de 800 VUs
- **Upstream:** Escalar a partir de 400 VUs
- **Upstream-Camel:** Escalar a partir de 300 VUs

### Arquitetura Híbrida Sugerida
```
Load Balancer
├── APIs Críticas → Downstream (8080)
├── APIs Gerais → Upstream (8081)
└── APIs Complexas → Upstream-Camel (8082)
```

## 🛠️ Desenvolvimento

### Estrutura dos Projetos
- **downstream/** - Serviço Quarkus básico com banco de dados
- **upstream/** - Serviço Quarkus que consome downstream
- **upstream-camel/** - Serviço Quarkus com rotas Camel

### Tecnologias
- **Framework:** Quarkus 3.x
- **Linguagem:** Java 21
- **Banco:** PostgreSQL
- **Integração:** Apache Camel 4.x
- **Container:** Docker
- **Testes:** k6

## 📚 Documentação Adicional

- [Quarkus Documentation](https://quarkus.io/guides/)
- [Apache Camel Documentation](https://camel.apache.org/manual/)
- [k6 Documentation](https://k6.io/docs/)
- [Docker Compose Reference](https://docs.docker.com/compose/)

## 🤝 Contribuição

1. Faça fork do projeto
2. Crie branch para sua feature
3. Execute testes de performance
4. Submit pull request com resultados

## 📄 Licença

Este projeto é para fins educacionais e de análise de performance.

---

**Última Atualização:** Maio 1, 2026  
**Performance Tests:** Concluídos e validados para produção  
**Status:** ✅ Production Ready
