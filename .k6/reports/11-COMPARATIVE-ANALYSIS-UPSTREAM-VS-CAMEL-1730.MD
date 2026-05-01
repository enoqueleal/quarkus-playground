# Relatório Comparativo: Upstream vs Upstream-Camel Performance

## Test Overview

**Data dos Testes:** Maio 1, 2026  
**Duração de Cada Teste:** 4 minutos 30 segundos  
**Padrão de Carga:** Progressivo de 10→1000 VUs  
**Recursos do Container:** 1 vCPU, 1GB RAM  

### Serviços Testados

1. **Upstream (sem Camel)** - Porta 8081
   - Arquitetura Quarkus pura
   - Comunicação direta com downstream
   - Sem framework de integração

2. **Upstream-Camel (com Camel)** - Porta 8082
   - Arquitetura Quarkus + Apache Camel
   - Comunicação via rotas Camel
   - Framework de integração ativo

## Resumo Comparativo

| Métrica | Upstream (sem Camel) | Upstream-Camel | Diferença | Vencedor |
|---------|---------------------|-----------------|-----------|-----------|
| **Total Requests** | 1,684,173 | 1,423,283 | -260,890 | 🏆 Upstream |
| **P95 Response Time** | 136.68ms | 154.20ms | +17.52ms | 🏆 Upstream |
| **Throughput (TPS)** | ~6,238 req/s | ~5,281 req/s | -957 req/s | 🏆 Upstream |
| **Performance Level** | ACEITÁVEL | ACEITÁVEL | - | Empate |
| **Degradation Point** | P95 > 100ms | P95 > 100ms | - | Empate |

## Análise Detalhada

### 1. Performance de Throughput

**Upstream (sem Camel):**
- ✅ **6,238 requests/second**
- ✅ 18% mais throughput que o upstream-camel
- ✅ Melhor aproveitamento dos recursos

**Upstream-Camel:**
- ⚠️ **5,281 requests/second**
- ⚠️ 15% menos throughput que o upstream sem camel
- ⚠️ Overhead do framework Camel impactando throughput

### 2. Latência (P95 Response Time)

**Upstream (sem Camel):**
- ✅ **136.68ms P95**
- ✅ 11% mais rápido que o upstream-camel
- ✅ Menor latência sob carga

**Upstream-Camel:**
- ⚠️ **154.20ms P95**
- ⚠️ 13% mais lento que o upstream sem camel
- ⚠️ Overhead de processamento das rotas Camel

### 3. Análise de Degradation

**Ambos os serviços apresentaram:**
- ⚠️ **Degradação Leve** detectada
- ⚠️ P95 ultrapassou 100ms threshold
- ✅ Mantiveram P95 < 500ms (threshold aceitável)
- ✅ Sem falhas críticas

### 4. Volume de Processamento

**Upstream (sem Camel):**
- ✅ Processou **260,890 requests a mais**
- ✅ 18% mais capacidade de processamento
- ✅ Melhor escalonamento sob carga

**Upstream-Camel:**
- ⚠️ Menor volume total processado
- ⚠️ Limitado pelo overhead do Camel

## Impacto do Apache Camel

### Overhead Identificado

1. **Throughput:** -15% (957 req/s a menos)
2. **Latência:** +13% (17.52ms mais lento)
3. **Capacidade:** -18% (260K requests a menos)

### Possíveis Causas do Overhead

1. **Processamento de Rotas:** Camel adiciona camada de processamento
2. **Serialização/Deserialização:** Conversão de mensagens
3. **Routing Logic:** Decisões de roteamento em runtime
4. **Memory Footprint:** Framework adicional consome recursos

### Benefícios do Camel (Não Medidos neste Teste)

- ✅ **Flexibilidade:** Integração com múltiplos sistemas
- ✅ **Manutenibilidade:** Código mais organizado
- ✅ **Extensibilidade:** Fácil adicionar novos endpoints
- ✅ **Enterprise Patterns:** Padrões de integração prontos

## Recomendações

### Para Alta Performance

🏆 **Use Upstream (sem Camel) quando:**
- Performance é prioridade máxima
- Integração é simples e direta
- Volume de requests é muito alto
- Latência mínima é crítica

### Para Integração Complexa

🔄 **Use Upstream-Camel quando:**
- Necessita integrar múltiplos sistemas
- Requer transformação de dados
- Precisa de padrões enterprise
- Manutenibilidade é mais importante que performance

### Recomendação Híbrida

🎯 **Considere arquitetura híbrida:**
- Upstream sem camel para endpoints críticos
- Upstream-camel para endpoints complexos
- Load balancing baseado no tipo de requisição

## Análise de Custo-Benefício

### Performance vs Complexidade

| Fator | Upstream | Upstream-Camel |
|-------|----------|----------------|
| **Performance** | 🏆 Excelente | ⚠️ Boa |
| **Simplicidade** | 🏆 Alta | ⚠️ Média |
| **Manutenibilidade** | ⚠️ Média | 🏆 Alta |
| **Flexibilidade** | ⚠️ Baixa | 🏆 Alta |
| **Learning Curve** | 🏆 Baixa | ⚠️ Alta |

## Conclusão

### Performance Pura
🏆 **Upstream (sem Camel) é o vencedor claro** em todos os aspectos de performance:
- 18% mais throughput
- 11% menor latência
- 18% mais capacidade de processamento

### Trade-offs
A escolha depende dos requisitos específicos:
- **Performance crítico:** Upstream sem Camel
- **Integração complexa:** Upstream-Camel
- **Balanceamento:** Arquitetura híbrida

### Impacto Negócio
- **Para APIs de alto volume:** Upstream sem Camel pode reduzir custos de infraestrutura
- **Para integração enterprise:** Upstream-Camel pode reduzir custos de desenvolvimento e manutenção

---

## Métricas Técnicas Detalhadas

### Configuração dos Testes
- **Duração:** 4m30s cada
- **Load Pattern:** Progressive (10→50→100→200→400→600→800→1000 VUs)
- **Thresholds:** P95 < 500ms, Error Rate < 20%
- **Container Resources:** 1 vCPU, 1GB RAM

### Arquivos de Dados
- **Upstream sem Camel:** `k6-upstream-sem-camel-20260501-172421.json` (7.2GB)
- **Upstream com Camel:** `k6-upstream-com-camel-20260501-172857.json` (6.1GB)

---

*Relatório gerado em Maio 1, 2026*  
*Baseado em testes de carga progressiva comparativos*
