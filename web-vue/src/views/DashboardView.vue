<template>
  <div class="dashboard-content">
    <div class="header-content">
      <h1>Próximos Eventos</h1>
      <div class="search-box">
        <input 
          type="text" 
          v-model="termoBusca" 
          placeholder="Buscar evento..." 
          maxlength="50" 
        />
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <p>Carregando eventos...</p>
    </div>

    <div v-else class="grid">
      <div v-for="evento in eventosFiltrados" :key="evento.id" class="card-box card-evento">
        <div class="card-header">
           <div class="header-top">
             <h3 :title="evento.nome">{{ evento.nome }}</h3>
             <button @click.stop="excluirEvento(evento.id)" class="btn-trash" title="Excluir Evento">🗑️</button>
           </div>
           <span class="price-badge">R$ {{ evento.preco ? evento.preco.toFixed(2) : '0.00' }}</span>
        </div>
        
        <div class="card-body">
          <p><strong>📅 Data:</strong> {{ formatarData(evento.dataEvento) }}</p>
          <p class="info-row">
            <strong>📍 Local:</strong> 
            <span class="truncate" :title="evento.localizacao">
              {{ evento.localizacao }}
            </span>
          </p>
          
          <div class="disponibilidade">
             <span>Ingressos: <strong>{{ evento.ingressosDisponiveis || 0 }}</strong> / {{ evento.numeroMaximoIngressos }}</span>
             <div class="progress-bar">
               <div class="progress-fill" :style="{ width: calcularPorcentagem(evento) + '%' }"></div>
             </div>
          </div>
        </div>

        <div class="card-actions">
          <button 
            @click="abrirModal('comprar', evento)" 
            class="btn-primary btn-full" 
            :disabled="(evento.ingressosDisponiveis || 0) <= 0">
            {{ (evento.ingressosDisponiveis || 0) > 0 ? 'COMPRAR' : 'ESGOTADO' }}
          </button>
          
          <button @click="abrirModal('stress', evento)" class="btn-stress" title="Stress Test">
            ⚡
          </button>
        </div>
      </div>
    </div>

    <div v-if="modal.aberto" class="modal-overlay">
      <div class="modal-content">
        <h3 :title="modal.titulo">{{ modal.titulo }}</h3>
        
        <div class="form-group">
          <label>{{ modal.label }}</label>
          <input 
            type="tel" 
            v-model="modal.valor" 
            @input="validarApenasNumeros"
            @keydown.enter="confirmarModal"
            ref="inputModal"
            placeholder="Digite a quantidade"
          />
          <small class="info-text">Máximo permitido: {{ modal.max }}</small>
          <small v-if="modal.erro" class="error-msg">{{ modal.erro }}</small>
        </div>

        <div class="modal-actions">
          <button @click="fecharModal" class="btn-danger">Cancelar</button>
          <button @click="confirmarModal" class="btn-primary">Confirmar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import axios from 'axios';
import api from '../services/api';
import { store } from '../store';

const eventos = ref([]);
const loading = ref(false);
const termoBusca = ref('');

const modal = ref({
  aberto: false,
  tipo: '',
  titulo: '',
  label: '',
  valor: 1,
  min: 1,
  max: 999999,
  step: 1,
  eventoAlvo: null,
  erro: ''
});
const inputModal = ref(null);

const eventosFiltrados = computed(() => {
  if (!termoBusca.value) return eventos.value;
  const termo = termoBusca.value.toLowerCase();
  return eventos.value.filter(e => 
    e.nome.toLowerCase().includes(termo) || 
    e.localizacao.toLowerCase().includes(termo)
  );
});

const formatarData = (dataObj) => {
  if (!dataObj) return 'A definir';
  if (typeof dataObj === 'string') return new Date(dataObj).toLocaleString('pt-BR');
  if (dataObj.date) {
    const { day, month, year } = dataObj.date;
    const { hour, minute } = dataObj.time || { hour: 0, minute: 0 };
    return `${day.toString().padStart(2,'0')}/${month.toString().padStart(2,'0')}/${year} às ${hour.toString().padStart(2,'0')}:${minute.toString().padStart(2,'0')}`;
  }
  return 'Data inválida';
};

const calcularPorcentagem = (ev) => {
  if (!ev.numeroMaximoIngressos) return 0;
  return ((ev.ingressosDisponiveis || 0) / ev.numeroMaximoIngressos) * 100;
};

const carregarEventos = async () => {
  loading.value = true;
  try {
    const res = await axios.get('http://localhost:8082/eventos');
    eventos.value = res.data;
  } catch (error) {
    console.error("Erro ao listar eventos:", error);
  } finally {
    loading.value = false;
  }
};

const atualizarDadosUsuario = async () => {
  if (!store.usuario.id) return;
  try {
    const res = await api.get(`/usuario?acao=buscar&id=${store.usuario.id}`);
    if (res.data.status === 'SUCESSO') {
        store.fazerLogin(res.data.usuario);
    }
  } catch (e) {
    console.error("Erro ao sincronizar saldo", e);
  }
};

const excluirEvento = async (id) => {
  if (!confirm("Tem certeza que deseja EXCLUIR este evento?")) return;
  try {
    const res = await api.post('/evento', { id }, { params: { acao: 'excluir' } });
    if (res.data.status === 'SUCESSO') {
      alert("Evento excluído!");
      carregarEventos();
    } else {
      alert("Erro: " + res.data.mensagem);
    }
  } catch (e) {
    alert("Erro ao excluir.");
  }
};

const abrirModal = (tipo, evento) => {
  modal.value = { 
    aberto: true, 
    tipo, 
    titulo: tipo === 'comprar' ? `Comprar: ${evento.nome}` : 'Simular Stress Test', 
    label: 'Quantidade:', 
    valor: tipo === 'stress' ? 50 : 1, 
    min: 1, 
    max: tipo === 'stress' ? 500 : (evento.ingressosDisponiveis || 1), 
    step: 1, 
    eventoAlvo: evento, 
    erro: '' 
  };
  nextTick(() => { if(inputModal.value) inputModal.value.focus(); });
};

const fecharModal = () => { modal.value.aberto = false; };

const confirmarModal = async () => {
  const valor = parseInt(modal.value.valor);
  if (isNaN(valor) || valor < modal.value.min) { 
      modal.value.erro = `Valor inválido.`; 
      return; 
  }
  
  if (modal.value.tipo === 'comprar') {
      await executarCompra(modal.value.eventoAlvo, valor);
  } else {
      await executarStress(modal.value.eventoAlvo.id, valor);
  }
  
  fecharModal();
};

const validarApenasNumeros = (e) => {
  let val = e.target.value.replace(/\D/g, '');
  if (val.length > 7) val = val.slice(0, 7);
  modal.value.valor = val === '' ? '' : parseInt(val);
  e.target.value = val;
};

const executarCompra = async (evento, quantidade) => {
  if (store.usuario.saldo < evento.preco * quantidade) { 
      alert(`Saldo insuficiente.`); 
      return; 
  }
  
  try {
    const res = await fetch('http://localhost:8081/pedido?acao=comprar', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        usuarioId: store.usuario.id,
        eventoId: evento.id,
        quantidade,
        precoUnitario: evento.preco
      })
    });
    
    const data = await res.json();
    if (data.status === 'SUCESSO') {
      alert(`Pedido #${data.pedidoId} enviado!`);
      await atualizarDadosUsuario();
      await carregarEventos();
    } else {
      alert("Erro: " + data.mensagem);
    }
  } catch (e) {
    alert("Erro de conexão.");
  }
};

const executarStress = async (eventoId, quantidadePedidos) => {
  loading.value = true;

  try {
    const valorNecessario = 50000.00;
    await api.post('/usuario', { 
      usuarioId: store.usuario.id, 
      valor: valorNecessario 
    }, { params: { acao: 'adicionarSaldo' } });
    
    store.usuario.saldo += valorNecessario;
    localStorage.setItem('usuario_ticketpro', JSON.stringify(store.usuario));
    
  } catch (e) {
    alert("Erro ao preparar saldo para o Stress Test.");
    loading.value = false;
    return;
  }

  const requests = [];
  for (let i = 0; i < quantidadePedidos; i++) {
    const req = fetch('http://localhost:8081/pedido?acao=comprar', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        usuarioId: store.usuario.id,
        eventoId: eventoId,
        quantidade: 1, 
        precoUnitario: 100.00 
      })
    }).then(res => res.json()).catch(err => ({ status: 'ERRO_REDE' }));
    
    requests.push(req);
  }

  try {
    const resultados = await Promise.all(requests);
    const sucessos = resultados.filter(r => r.status === 'SUCESSO').length;
    const falhas = quantidadePedidos - sucessos;

    alert(`Stress Test Concluído!\n✅ Aceitos: ${sucessos}\n❌ Recusados: ${falhas}`);
    
    await atualizarDadosUsuario(); 
    await carregarEventos();       

  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  carregarEventos();
});
</script>

<style scoped>
.header-content { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.search-box input { width: 300px; border-radius: 20px; }
.loading-state { text-align: center; color: #777; font-size: 1.2rem; margin-top: 50px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 25px; }
.card-evento { display: flex; flex-direction: column; height: 100%; transition: transform 0.2s; }
.card-evento:hover { transform: translateY(-3px); }
.header-top { display: flex; justify-content: space-between; width: 100%; align-items: center; gap: 10px; }
.header-top h3 { margin: 0; font-size: 1.1rem; color: var(--text); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex: 1; min-width: 0; }
.btn-trash { background: none; border: none; cursor: pointer; font-size: 1.2rem; padding: 0; opacity: 0.6; transition: 0.2s; flex-shrink: 0; }
.btn-trash:hover { opacity: 1; transform: scale(1.1); }
.price-badge { background: #f0f0f0; padding: 4px 8px; border-radius: 4px; font-weight: bold; font-size: 0.9rem; color: #333; margin-top: 8px; display: inline-block; }
.card-body p { margin: 8px 0; color: #666; font-size: 0.95rem; }
.info-row { display: flex; align-items: center; margin: 8px 0; color: #666; font-size: 0.95rem; }
.info-row strong { white-space: nowrap; margin-right: 5px; }
.truncate { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex: 1; min-width: 0; display: block; }
.disponibilidade { margin: 15px 0; font-size: 0.85rem; color: #555; }
.progress-bar { width: 100%; height: 6px; background: #eee; border-radius: 3px; margin-top: 5px; overflow: hidden; }
.progress-fill { height: 100%; background: var(--success); transition: width 0.4s ease; }
.card-actions { display: flex; gap: 8px; margin-top: auto; padding-top: 15px; }
.btn-full { flex: 1; }
.btn-stress { background: #9b59b6; color: white; width: 45px; display: flex; align-items: center; justify-content: center; font-size: 1.1rem; }
.btn-stress:hover { background: #8e44ad; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); backdrop-filter: blur(2px); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; padding: 25px; border-radius: 8px; width: 320px; box-shadow: 0 10px 25px rgba(0,0,0,0.2); }
.modal-content h3 { margin-top: 0; margin-bottom: 20px; color: #333; text-align: center; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 100%; }
.error-msg { color: var(--danger); font-size: 0.85rem; margin-top: 5px; display: block; }
.info-text { display: block; font-size: 0.8rem; color: #777; margin-top: 5px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
input[type=tel] { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 1.2rem; text-align: center; box-sizing: border-box; }
input[type=tel]:focus { border-color: #3498db; outline: none; }
</style>