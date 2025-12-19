<script setup>
import { RouterView, useRouter } from 'vue-router'
import { ref, computed, nextTick } from 'vue'
import api from './services/api'
import { store } from './store'

const router = useRouter()
const inputSaldo = ref(null)

const modal = ref({
  aberto: false,
  valorDisplay: '',
  erro: ''
})

const iniciais = computed(() => {
  const nome = store.usuario.nome || 'U'
  return nome.substring(0, 2).toUpperCase()
})

const abrirModalSaldo = () => {
  modal.value.aberto = true
  modal.value.valorDisplay = '50,00'
  modal.value.erro = ''
  nextTick(() => { if (inputSaldo.value) inputSaldo.value.focus() })
}

const fecharModal = () => { modal.value.aberto = false }

const formatarMoeda = (e) => {
  const target = e.target
  let valor = target.value.replace(/\D/g, "")
  if (valor.length > 7) valor = valor.slice(0, 7)
  if (valor === "") valor = "0"
  const numero = parseInt(valor) / 100
  modal.value.valorDisplay = numero.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const confirmarSaldo = async () => {
  const valorLimpo = modal.value.valorDisplay.replace(/\./g, '').replace(',', '.')
  const valor = parseFloat(valorLimpo)

  if (isNaN(valor) || valor <= 0) { modal.value.erro = "Digite um valor válido maior que zero."; return }

  try {
    const res = await api.post('/usuario', { usuarioId: store.usuario.id, valor: valor }, { params: { acao: 'adicionarSaldo' } })
    if (res.data.status === 'SUCESSO') {
      store.usuario.saldo = res.data.novoSaldo
      localStorage.setItem('usuario_ticketpro', JSON.stringify(store.usuario))
      
      fecharModal()
      alert("Saldo adicionado com sucesso!")
    } else {
      modal.value.erro = res.data.mensagem || "Erro ao adicionar saldo."
    }
  } catch (e) {
    modal.value.erro = "Erro de conexão com o servidor."
  }
}

const logout = () => {
  store.fazerLogout()
  router.push('/')
}
</script>

<template>
  <div v-if="store.logado" class="main-layout">
    
    <aside class="sidebar">
      <div class="user-info">
        <div class="avatar">{{ iniciais }}</div>
        <h3 :title="store.usuario.nome">{{ store.usuario.nome }}</h3>
        <p class="email" :title="store.usuario.email">{{ store.usuario.email }}</p>
      </div>
      
      <div class="saldo-box">
        <span>Saldo Atual</span>
        <h2>R$ {{ store.usuario.saldo ? parseFloat(store.usuario.saldo).toFixed(2).replace('.', ',') : '0,00' }}</h2>
        <button @click="abrirModalSaldo" class="btn-add-saldo">+ Adicionar Saldo</button>
      </div>

      <nav class="menu">
        <router-link to="/dashboard" class="menu-item">📅 Eventos</router-link>
        <router-link to="/meus-pedidos" class="menu-item">🎟️ Meus Pedidos</router-link>
        <router-link to="/evento/novo" class="menu-item">➕ Criar Evento</router-link>
        <button @click="logout" class="menu-item btn-logout">🚪 Sair</button>
      </nav>
    </aside>

    <main class="content-area">
      <RouterView />
    </main>

    <div v-if="modal.aberto" class="modal-overlay">
      <div class="modal-content">
        <h3>Adicionar Saldo</h3>
        <div class="form-group">
          <label>Valor (R$)</label>
          <input 
            type="tel" 
            v-model="modal.valorDisplay" 
            ref="inputSaldo"
            placeholder="0,00"
            @input="formatarMoeda"
            @keydown.enter="confirmarSaldo"
            class="input-moeda"
          />
          <small class="hint">Digite apenas números. Máximo: R$ 10.000,00</small>
          <small v-if="modal.erro" class="error-msg">{{ modal.erro }}</small>
        </div>
        <div class="modal-actions">
          <button @click="fecharModal" class="btn-danger">Cancelar</button>
          <button @click="confirmarSaldo" class="btn-primary">Confirmar</button>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="full-screen">
    <RouterView />
  </div>
</template>

<style scoped>
.main-layout { display: flex; height: 100vh; width: 100vw; overflow: hidden; }
.sidebar { width: 280px; background-color: var(--bg-sidebar); color: white; display: flex; flex-direction: column; padding: 20px; flex-shrink: 0; box-shadow: 2px 0 5px rgba(0,0,0,0.1); }
.content-area { flex: 1; padding: 30px; overflow-y: auto; }
.user-info { text-align: center; margin-bottom: 30px; overflow: hidden; }
.avatar { width: 70px; height: 70px; background: #3498db; border-radius: 50%; margin: 0 auto 10px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; font-weight: bold; }
.user-info h3 { margin: 10px 0 5px; font-size: 1.1rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 100%; }
.user-info .email { color: #bdc3c7; font-size: 0.9rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 100%; display: block; }
.saldo-box { background: rgba(255,255,255,0.05); padding: 15px; border-radius: 8px; text-align: center; margin-bottom: 20px; border: 1px solid rgba(255,255,255,0.1); }
.saldo-box h2 { color: #2ecc71; margin: 5px 0; }
.btn-add-saldo { background: #27ae60; color: white; width: 100%; margin-top: 5px; padding: 8px; border: none; cursor: pointer; border-radius: 4px; font-weight: bold; transition: background 0.2s; }
.btn-add-saldo:hover { background: #219150; }
.menu { display: flex; flex-direction: column; gap: 10px; }
.menu-item { color: #ecf0f1; padding: 12px; text-decoration: none; border-radius: 6px; text-align: left; }
.menu-item:hover, .menu-item.router-link-active { background: rgba(255,255,255,0.1); }
.btn-logout { background: #c0392b; color: white; margin-top: auto; border: none; cursor: pointer; text-align: left; padding: 12px; border-radius: 6px; }
.full-screen { width: 100%; height: 100vh; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.7); backdrop-filter: blur(2px); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; padding: 25px; border-radius: 8px; width: 320px; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }
.modal-content h3 { margin-top: 0; margin-bottom: 20px; color: #333; text-align: center; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; font-weight: bold; margin-bottom: 5px; color: #555; }
.form-group input { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 1.2rem; text-align: center; box-sizing: border-box; background: white; color: #333; }
.input-moeda { font-weight: bold; color: #2c3e50; letter-spacing: 1px; }
.hint { display: block; font-size: 0.8rem; color: #777; margin-top: 5px; text-align: center; }
.error-msg { display: block; color: #e74c3c; font-size: 0.85rem; margin-top: 5px; text-align: center; font-weight: bold; }
.modal-actions { display: flex; justify-content: space-between; gap: 10px; }
.btn-primary { background: #3498db; color: white; border: none; padding: 10px; border-radius: 4px; cursor: pointer; flex: 1; font-weight: bold; }
.btn-danger { background: #e74c3c; color: white; border: none; padding: 10px; border-radius: 4px; cursor: pointer; flex: 1; font-weight: bold; }
</style>