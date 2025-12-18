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

const pedidos = ref<Pedido[]>([])
const usuario = JSON.parse(localStorage.getItem('usuario_ticketpro') || '{}')
let pollingInterval: number | null = null

const carregarHistorico = async () => {
  try {
    const res = await fetch(`http://localhost:8081/pedido?acao=listarHistorico&usuarioId=${usuario.id}`)
    pedidos.value = await res.json()
  } catch (e) {
    console.error("Erro ao atualizar histórico", e)
  }
}

const getStatusClass = (status: string) => {
  switch(status) {
    case 'APROVADO': return 'status-aprovado'
    case 'PENDENTE': return 'status-pendente'
    default: return 'status-erro'
  }
}

onMounted(() => {
  carregarHistorico()
  pollingInterval = setInterval(carregarHistorico, 2000)
})

onUnmounted(() => {
  if (pollingInterval) clearInterval(pollingInterval)
})
</script>

<template>
  <div class="container">
    <h1>Meus Pedidos</h1>
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Evento</th>
          <th>Qtd</th>
          <th>Total</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in pedidos" :key="p.id">
          <td>#{{ p.id }}</td>
          <td>{{ p.eventoId }}</td> <td>{{ p.quantidade }}</td>
          <td>R$ {{ p.valorTotal }}</td>
          <td>
            <span :class="['badge', getStatusClass(p.status)]">
              {{ p.status }}
            </span>
            <small v-if="p.mensagemErro" class="erro-msg">({{ p.mensagemErro }})</small>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.container { padding: 20px; }
table { width: 100%; border-collapse: collapse; margin-top: 20px; }
th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
th { background-color: #f4f4f4; }
.badge { padding: 5px 10px; border-radius: 4px; font-weight: bold; color: white; font-size: 0.9em;}
.status-aprovado { background-color: #28a745; }
.status-pendente { background-color: #ffc107; color: #333; }
.status-erro { background-color: #dc3545; }
.erro-msg { display: block; color: #dc3545; font-size: 0.8em; margin-top: 4px; }
</style>