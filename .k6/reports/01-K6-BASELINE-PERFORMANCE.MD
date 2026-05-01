# Relatório de Performance - K6 Test

## Resumo Executivo

Teste de performance realizado no endpoint `/api/random-names` do serviço Quarkus com duração de 3 minutos.

### Configuração do Teste
- **Duração Total**: 3 minutos
- **Virtual Users**: Ramp-up de 1 para 10 usuários em 30s, mantido por 2 minutos, ramp-down em 30s
- **Endpoint**: `http://localhost:8081/api/random-names`
- **Método**: GET
- **Thresholds**: 
  - 95% das respostas < 500ms ✅
  - Taxa de erro < 10% ✅

## Resultados Principais

### Métricas de Desempenho
| Métrica | Valor | Status |
|---------|-------|--------|
| **Total de Requests** | 1,514 | ✅ |
| **Taxa de Sucesso** | 100% (0 falhas) | ✅ |
| **Throughput** | 8.37 req/s | ✅ |
| **Tempo Médio de Resposta** | 3.05ms | ✅ |
| **95th Percentile** | 4.97ms | ✅ |
| **Tempo Máximo** | 27.53ms | ✅ |

### Análise de Tempos de Resposta
- **Mínimo**: 1.05ms
- **Máximo**: 27.53ms
- **Mediana**: 2.76ms
- **90th Percentile**: 4.46ms
- **95th Percentile**: 4.97ms

### Verificações (Checks)
| Check | Taxa de Sucesso | Resultado |
|-------|----------------|-----------|
| Status is 200 | 100% | ✅ |
| Response time < 500ms | 100% | ✅ |
| Response has content | 100% | ✅ |

## Análise Detalhada

### Performance Exceptional 🟢
O serviço demonstrou performance excepcional com:
- **Tempo de resposta médio de apenas 3.05ms** - extremamente rápido
- **95th percentile de 4.97ms** - bem abaixo do threshold de 500ms
- **Zero erros** durante todo o teste
- **100% de disponibilidade** com 1,514 requests bem-sucedidos

### Comportamento Sob Carga
- **Ramp-up (0-30s)**: O serviço manteve performance estável ao aumentar de 1 para 10 VUs
- **Carga constante (30s-2m30s)**: Performance consistente com 10 VUs simultâneos
- **Ramp-down (2m30s-3m)**: Performance mantida durante redução gradual de usuários

### Análise de Rede
- **Dados Enviados**: 170 kB (938 B/s)
- **Dados Recebidos**: 345 kB (1.9 kB/s)
- **Protocolo**: HTTP/1.1

## Recomendações

### ✅ Pontos Fortes
1. **Performance excelente**: Tempos de resposta consistentemente baixos
2. **Estabilidade**: Zero erros sob carga
3. **Escalabilidade**: Performance mantida com múltiplos usuários simultâneos
4. **Eficiência**: Baixo consumo de recursos

### 🔄 Oportunidades de Melhoria
1. **Monitoramento**: Implementar métricas mais detalhadas
2. **Cache**: Considerar estratégias de cache se o volume aumentar
3. **Load Testing**: Testar com cargas mais altas (>10 VUs)

## Conclusão

O endpoint `/api/random-names` apresentou **performance excepcional** durante o teste de 3 minutos, com tempos de resposta extremamente baixos (média de 3ms) e 100% de taxa de sucesso. O serviço demonstrou excelente capacidade de handle múltiplas requisições simultâneas sem degradação de performance.

**Status**: ✅ **APROVADO** - O serviço atende e excede significativamente os requisitos de performance estabelecidos.

---
*Relatório gerado em: 2026-05-01*  
*Ferramenta: K6 v1.7.1*  
*Duração do teste: 3 minutos*
