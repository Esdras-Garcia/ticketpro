<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="user-info">
        <div class="avatar">{{ iniciais }}</div>
        <h3>{{ usuario.nome }}</h3>
        <p class="email">{{ usuario.email }}</p>
      </div>
      
      <div class="saldo-box">
        <span>Saldo Atual</span>
        <h2>R$ {{ usuario.saldo ? usuario.saldo.toFixed(2) : '0.00' }}</h2>
        <button @click="adicionarSaldo" class="btn-add-saldo">+ Adicionar Saldo</button>
      </div>

      <nav class="menu">
        <router-link to="/perfil" class="menu-item">👤 Meu Perfil</router-link>
        <router-link to="/meus-pedidos" class="menu-item">🎟️ Meus Pedidos</router-link>
        <router-link to="/evento/novo" class="menu-item">📅 Criar Evento</router-link> 
        <button @click="logout" class="menu-item btn-logout">🚪 Sair</button>
      </nav>
    </aside>

    <main class="content">
      <div class="header-content">
        <h1>Próximos Eventos</h1>
        
        <div class="search-box">
          <input 
            type="text" 
            v-model="termoBusca" 
            placeholder="Buscar evento por nome ou local..."
          />
        </div>
      </div>

      <div v-if="loading" class="loading">Carregando eventos...</div>

      <div v-else class="grid">
        <div v-for="evento in eventosFiltrados" :key="evento.id" class="card">
          <div class="card-header">
            <h3>{{ evento.nome }}</h3>
            <span class="badge">R$ 100,00</span> </div>
          
          <div class="card-body">
            <p><strong>📅 Data:</strong> {{ formatarData(evento.dataEvento) }}</p>
            <p><strong>📍 Local:</strong> {{ evento.localizacao }}</p>
          </div>

          <button @click="comprar(evento.id)" class="btn-comprar">
            COMPRAR INGRESSO
          </button>
        </div>
      </div>

      <p v-if="!loading && eventosFiltrados.length === 0" class="empty-state">
        Nenhum evento encontrado para "{{ termoBusca }}".
      </p>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const eventos = ref([]);
const loading = ref(false);
const termoBusca = ref('');

const usuarioStorage = localStorage.getItem('usuario_ticketpro');
const usuario = ref(usuarioStorage ? JSON.parse(usuarioStorage) : {});

const iniciais = computed(() => {
  const nome = usuario.value.nome || 'U';
  return nome.substring(0, 2).toUpperCase();
});

const eventosFiltrados = computed(() => {
  if (!termoBusca.value) return eventos.value;
  const termo = termoBusca.value.toLowerCase();
  return eventos.value.filter(e => 
    e.nome.toLowerCase().includes(termo) || 
    e.localizacao.toLowerCase().includes(termo)
  );
});

const formatarData = (dataObj) => {
  if (!dataObj) return 'Data a definir';
  
  if (typeof dataObj === 'string') return new Date(dataObj).toLocaleString('pt-BR');

  if (dataObj.date && dataObj.time) {
    const { day, month, year } = dataObj.date;
    const { hour, minute } = dataObj.time;
    const pad = (n) => n.toString().padStart(2, '0');
    return `${pad(day)}/${pad(month)}/${year} às ${pad(hour)}:${pad(minute)}`;
  }

  return 'Formato desconhecido';
};

const carregarEventos = async () => {
  loading.value = true;
  try {
    const res = await api.get('/evento', { params: { acao: 'listar' } });
    eventos.value = res.data;
  } catch (error) {
    console.error("Erro ao listar eventos:", error);
    alert("Não foi possível carregar os eventos.");
  } finally {
    loading.value = false;
  }
};

const adicionarSaldo = async () => {
  const valorStr = prompt("Quanto deseja adicionar ao saldo?", "50.00");
  if (!valorStr) return;
  
  const valor = parseFloat(valorStr.replace(',', '.'));
  if (isNaN(valor) || valor <= 0) return alert("Valor inválido");

  try {
    alert(`Enviando solicitação de R$ ${valor}... (Implementar no Backend)`);
    usuario.value.saldo += valor;
    localStorage.setItem('usuario_ticketpro', JSON.stringify(usuario.value));
  } catch (e) {
    alert("Erro ao adicionar saldo");
  }
};

const comprar = async (eventoId) => {
  if (!confirm("Confirmar compra de 1 ingresso?")) return;
  try {
    const res = await api.post('/pedido', {
      usuarioId: usuario.value.id,
      eventoId: eventoId,
      quantidade: 1
    }, {
      params: { acao: 'comprar' }
    });
    
    if (res.data.status === 'SUCESSO' || res.data.status === 'PROCESSANDO') {
      alert(res.data.mensagem || 'Pedido realizado!');
    } else {
      alert("Erro: " + res.data.mensagem);
    }
  } catch (e) {
    console.error(e);
    alert("Erro ao processar compra.");
  }
};

const logout = () => {
  localStorage.removeItem('usuario_ticketpro');
  router.push('/');
};

onMounted(() => {
  if (!usuario.value.id) router.push('/');
  carregarEventos();
});
</script>

<style scoped>
.dashboard-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f4f6f8;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.sidebar {
  width: 280px;
  background: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-shadow: 2px 0 5px rgba(0,0,0,0.1);
}

.user-info {
  text-align: center;
  margin-bottom: 30px;
}

.avatar {
  width: 80px;
  height: 80px;
  background: #3498db;
  border-radius: 50%;
  margin: 0 auto 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
}

.saldo-box {
  background: rgba(255,255,255,0.1);
  padding: 15px;
  border-radius: 8px;
  text-align: center;
  margin-bottom: 30px;
}

.saldo-box h2 { color: #2ecc71; margin: 5px 0; }

.btn-add-saldo {
  background: #27ae60;
  border: none;
  color: white;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  width: 100%;
}

.menu { display: flex; flex-direction: column; gap: 10px; }

.menu-item {
  text-decoration: none;
  color: #ecf0f1;
  padding: 12px;
  border-radius: 6px;
  transition: background 0.2s;
  text-align: left;
}

.menu-item:hover { background: #34495e; }
.btn-logout { background: #c0392b; border: none; cursor: pointer; color: white; margin-top: auto; }

.content {
  flex: 1;
  padding: 40px;
  overflow-y: auto;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.search-box input {
  padding: 10px 15px;
  width: 300px;
  border: 1px solid #ddd;
  border-radius: 20px;
  outline: none;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 25px;
}

.card {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  transition: transform 0.2s;
  display: flex;
  flex-direction: column;
}

.card:hover { transform: translateY(-5px); }

.card-header {
  background: #3498db;
  color: white;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 { margin: 0; font-size: 1.1rem; }
.badge { background: rgba(0,0,0,0.2); padding: 4px 8px; border-radius: 4px; font-size: 0.9rem; }

.card-body { padding: 20px; flex: 1; }
.card-body p { margin: 10px 0; color: #555; }

.btn-comprar {
  background: #e67e22;
  color: white;
  border: none;
  padding: 15px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-comprar:hover { background: #d35400; }
</style>