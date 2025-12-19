<template>
  <div class="pedidos-view">
    <h1>Meus Pedidos</h1>
    
    <div class="card-box">
      <table v-if="pedidos.length > 0">
        <thead>
          <tr>
            <th>Número do Pedido</th>
            <th>Evento</th>
            <th>Qtd</th>
            <th>Valor Total</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in pedidos" :key="p.id">
            <td>#{{ p.id }}</td>
            <td>{{ getNomeEvento(p.eventoId) }}</td>
            <td>{{ p.quantidade }}</td>
            <td>R$ {{ p.valorTotal.toFixed(2) }}</td>
            <td>
              <span :class="['status-badge', p.status.toLowerCase()]">
                {{ p.status }}
              </span>
              <div v-if="p.mensagemErro" class="erro-msg">{{ p.mensagemErro }}</div>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else>Você ainda não realizou nenhum pedido.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

interface Pedido {
  id: number;
  eventoId: number;
  quantidade: number;
  valorTotal: number;
  status: string;
  mensagemErro?: string;
}

interface Evento {
    id: number;
    nome: string;
}

const pedidos = ref<Pedido[]>([])
const eventosMap = ref<Record<number, string>>({})
let pollingInterval: number | null = null

const carregarEventos = async () => {
    try {
        const res = await fetch('http://localhost:8082/eventos') 
        const data: Evento[] = await res.json()
        const mapa: Record<number, string> = {}
        data.forEach(e => mapa[e.id] = e.nome)
        eventosMap.value = mapa
    } catch (e) {
        console.error("Erro ao carregar eventos", e)
    }
}

const carregarHistorico = async () => {
  const userStr = localStorage.getItem('usuario_ticketpro')
  if (!userStr) return 
  const usuario = JSON.parse(userStr)
  if (!usuario.id) return
  try {
    const res = await fetch(`http://localhost:8081/pedido?acao=listarHistorico&usuarioId=${usuario.id}`)
    if (res.ok) {
        pedidos.value = await res.json()
    }
  } catch (e) { 
    console.error(e) 
  }
}

const getNomeEvento = (id: number) => {
    return eventosMap.value[id] || `Evento #${id}`
}

onMounted(() => {
  carregarEventos()
  carregarHistorico()
  pollingInterval = setInterval(carregarHistorico, 3000)
})

onUnmounted(() => { 
  if (pollingInterval !== null) clearInterval(pollingInterval) 
})
</script>

<style scoped>
.status-badge { padding: 5px 10px; border-radius: 12px; font-size: 0.85rem; font-weight: bold; color: white; }
.status-badge.aprovado { background-color: var(--success); }
.status-badge.pendente { background-color: #f1c40f; color: #333; }
.status-badge.rejeitado, .status-badge.sem_estoque { background-color: var(--danger); }
.erro-msg { font-size: 0.8rem; color: var(--danger); margin-top: 5px; }
</style>