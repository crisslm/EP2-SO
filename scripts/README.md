# Verificações do benchmark

Na raiz do projeto, com JDK 21 ou superior e Bash no PATH:

```bash
bash scripts/test-benchmark.sh
```

As verificações compilam e exercitam o núcleo sem depender de JFreeChart ou da
interface gráfica. Cobrem falhas e interrupções dos workers, espera por todas as
threads, liberação de bloqueios e lotes normais com 100 threads. Não substituem
as 50 repetições de cada proporção exigidas para o relatório.

Os gerenciadores aguardam todos os workers antes de propagar uma falha. Uma
rodada com falha ou uma interrupção da thread principal encerra o experimento,
impedindo que um tempo inválido seja somado à média. Não há descarte silencioso
nem substituição de rodadas por zero.

Para executar as verificações sem influenciar as medições, use este script em
uma execução separada da coleta de resultados do relatório.
