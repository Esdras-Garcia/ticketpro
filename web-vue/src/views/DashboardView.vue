<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface Evento {
  id: number;
  nome: string;
  dataEvento: string;
  localizacao: string;
}

const eventos = ref<Evento[]>([])
const loading = ref(false)
const msg = ref('')

const usuario = JSON.parse(localStorage.getItem('usuario_ticketpro') || '{}')

const carregarEventos = async () => {
  const res = await fetch('http://localhost:8081/evento?acao=listar')
  eventos.value = await res.json()
}

const comprar = async (eventoId: number) => {
  if (!confirm("Confirmar compra de 1 ingresso por R$ 100,00?")) return;
  
  loading.value = true
  msg.value = "Enviando pedido..."

  try {
    const res = await fetch('http://localhost:8081/pedido?acao=comprar', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        usuarioId: usuario.id,
        eventoId: eventoId,
        quantidade: 1,
        precoUnitario: 100.00
      })
    })
    
    const data = await res.json()
    if (data.status === 'SUCESSO') {
      alert(`Pedido Enviado! ID: ${data.pedidoId}. Acompanhe em 'Meus Pedidos'.`)
    } else {
      alert("Erro: " + data.mensagem)
    }
  } catch (e) {
    alert("Erro de conexão")
  } finally {
    loading.value = false
    msg.value = ""
  }
}

onMounted(() => {
  carregarEventos()
})
</script>

<template>
  <div class="container">
    <h1>Próximos Eventos</h1>
    <p>Saldo Atual: R$ {{ usuario.saldo }}</p>

    <div class="grid">
      <div v-for="evento in eventos" :key="evento.id" class="card">
        <h3>{{ evento.nome }}</h3>
        <p>📅 {{ evento.dataEvento }}</p>
        <p>📍 {{ evento.localizacao }}</p>
        <button @click="comprar(evento.id)" :disabled="loading">
          COMPRAR (R$ 100)
        </button>
      </div>
    </div>
    <p v-if="eventos.length === 0">Nenhum evento encontrado.</p>
  </div>
</template>

<style scoped>
.container { padding: 20px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 20px; margin-top: 20px;}
.card { border: 1px solid #ddd; padding: 20px; border-radius: 8px; box-shadow: 2px 2px 10px rgba(0,0,0,0.1); }
button { background: #007bff; color: white; border: none; padding: 10px; width: 100%; cursor: pointer; margin-top: 10px;}
button:hover { background: #0056b3; }
</style>