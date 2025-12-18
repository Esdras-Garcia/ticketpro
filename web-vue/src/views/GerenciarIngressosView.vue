<template>
  <div class="container">
    <div class="card">
      <h2>Gerenciar Ingressos (Lote)</h2>

      <form @submit.prevent="gerarLote" class="form-grid">
        <div class="form-group">
          <label>ID do Evento</label>
          <input type="number" v-model.number="form.eventoId" required placeholder="Ex: 1" />
        </div>

        <div class="form-group">
          <label>Prefixo (Bloco/Setor)</label>
          <input type="text" v-model="form.prefixo" required placeholder="Ex: VIP" />
        </div>

        <div class="form-group">
          <label>Quantidade</label>
          <input type="number" v-model.number="form.quantidade" required min="1" />
        </div>

        <div class="form-group">
          <label>Preço (R$)</label>
          <input type="number" v-model.number="form.preco" step="0.01" required />
        </div>

        <div class="full-width">
           <button type="submit" :disabled="loading" class="btn-primary">
             {{ loading ? 'Processando...' : 'Gerar Lote' }}
           </button>
           
           <button type="button" @click="limparEstoque" class="btn-danger" v-if="form.eventoId">
             Limpar Estoque
           </button>
        </div>
      </form>
    </div>

    <div v-if="mensagem" :class="['alert', tipoMensagem]">
      {{ mensagem }}
    </div>

    <div class="card mt-4">
      <div class="header-lista">
        <h3>Ingressos do Evento {{ form.eventoId }}</h3>
        <button @click="listarIngressos" class="btn-secondary" :disabled="!form.eventoId">
          Atualizar Lista
        </button>
      </div>

      <table v-if="lista.length > 0">
        <thead>
          <tr>
            <th>ID</th>
            <th>Código</th>
            <th>Preço</th>
            <th>Disponível?</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in lista" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.codigoAssento }}</td>
            <td>R$ {{ item.preco.toFixed(2) }}</td>
            <td>
              <span :class="item.disponivel ? 'badge-verde' : 'badge-vermelho'">
                {{ item.disponivel ? 'Sim' : 'Vendido' }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-center">Nenhum ingresso encontrado para este evento.</p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import IngressoService from '../services/IngressoService';

const loading = ref(false);
const mensagem = ref('');
const tipoMensagem = ref('');
const lista = ref([]);

const form = reactive({
  eventoId: 1,
  prefixo: '',
  quantidade: 10,
  preco: 0.0
});

const mostrarMensagem = (msg, tipo = 'info') => {
  mensagem.value = msg;
  tipoMensagem.value = tipo;
  setTimeout(() => mensagem.value = '', 5000);
};

const gerarLote = async () => {
  loading.value = true;
  try {
    const resp = await IngressoService.gerarLote(form);
    
    if (resp.data.status === 'SUCESSO') {
      mostrarMensagem(resp.data.mensagem, 'success');
      listarIngressos();
    } else {
      mostrarMensagem(resp.data.mensagem || 'Erro ao gerar', 'error');
    }
  } catch (error) {
    mostrarMensagem('Erro na comunicação com o servidor.', 'error');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const listarIngressos = async () => {
  if (!form.eventoId) return;
  
  try {
    const resp = await IngressoService.listarPorEvento(form.eventoId);
    lista.value = resp.data;
  } catch (error) {
    console.error("Erro ao listar", error);
  }
};

const limparEstoque = async () => {
    if(!confirm("Tem certeza? Isso apagará ingressos não vendidos.")) return;
    
    try {
        const resp = await IngressoService.limparEstoque(form.eventoId);
        mostrarMensagem(resp.data.mensagem, resp.data.status === 'SUCESSO' ? 'success' : 'error');
        listarIngressos();
    } catch (e) {
        mostrarMensagem("Erro ao limpar estoque", 'error');
    }
}
</script>

<style scoped>
.container { max-width: 800px; margin: 0 auto; padding: 20px; }
.card { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.mt-4 { margin-top: 1.5rem; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.full-width { grid-column: span 2; display: flex; gap: 10px;}
.form-group { display: flex; flex-direction: column; }
label { font-weight: bold; margin-bottom: 5px; }
input { padding: 8px; border: 1px solid #ccc; border-radius: 4px; }

button { padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; color: white; font-weight: bold; }
.btn-primary { background-color: #3498db; flex: 1; }
.btn-primary:hover { background-color: #2980b9; }
.btn-secondary { background-color: #95a5a6; }
.btn-danger { background-color: #e74c3c; }

table { width: 100%; border-collapse: collapse; margin-top: 15px; }
th, td { padding: 10px; border-bottom: 1px solid #eee; text-align: left; }
.badge-verde { background: #d4edda; color: #155724; padding: 2px 6px; border-radius: 4px; font-size: 0.8em; }
.badge-vermelho { background: #f8d7da; color: #721c24; padding: 2px 6px; border-radius: 4px; font-size: 0.8em; }

.alert { padding: 10px; margin-top: 10px; border-radius: 4px; text-align: center; }
.success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
.error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
</style>