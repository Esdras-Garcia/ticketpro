<template>
  <div>
    <h1>Criar Novo Evento</h1>
    <div class="card-box" style="max-width: 600px;">
      <form @submit.prevent="salvarEvento">
        
        <div class="form-group">
          <label>Nome do Evento</label>
          <input 
            type="text" 
            v-model="form.nome" 
            required minlength="3" 
            maxlength="50" 
            @input="form.nome = form.nome.replace(/[^a-zA-Z0-9À-ÿ\s]/g, '')"
            placeholder="Ex: Show de Rock"
          />
          <small style="color: #95a5a6; font-size: 0.8rem; display: block; text-align: right; margin-top: 4px;">
            {{ form.nome.length }}/50
          </small>
        </div>

        <div class="form-group">
          <label>Data e Hora</label>
          <input type="datetime-local" v-model="form.dataEvento" required />
        </div>

        <div class="form-group">
          <label>Localização</label>
          <input 
            type="text" 
            v-model="form.localizacao" 
            required minlength="3" 
            maxlength="50" 
            @input="form.localizacao = form.localizacao.replace(/[^a-zA-Z0-9À-ÿ\s]/g, '')"
            placeholder="Ex: Arena Principal"
          />
          <small style="color: #95a5a6; font-size: 0.8rem; display: block; text-align: right; margin-top: 4px;">
            {{ form.localizacao.length }}/50
          </small>
        </div>

        <div style="display: flex; gap: 15px;">
          
          <div class="form-group" style="flex:1">
            <label>Lotação</label>
            <input 
              type="tel" 
              v-model="form.numeroMaximoIngressos" 
              required 
              placeholder="0"
              @input="formatarInteiro"
            />
          </div>

          <div class="form-group" style="flex:1">
            <label>Preço (R$)</label>
            <input 
              type="tel" 
              v-model="precoDisplay" 
              required 
              placeholder="0,00"
              @input="formatarMoeda"
            />
          </div>
        </div>

        <div style="display: flex; gap: 10px; margin-top: 10px;">
          <button type="button" class="btn-danger" @click="cancelar">Cancelar</button>
          <button type="submit" class="btn-primary" style="flex:1" :disabled="loading">
            {{ loading ? 'Salvando...' : 'Criar Evento' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const loading = ref(false);

const form = reactive({ 
  nome: '', 
  dataEvento: '', 
  localizacao: '', 
  numeroMaximoIngressos: '' 
});

const precoDisplay = ref('');

const formatarInteiro = (e) => {
  let val = e.target.value.replace(/\D/g, ''); 
  if (val.length > 6) val = val.slice(0, 6);
  form.numeroMaximoIngressos = val;
  e.target.value = val;
};

const formatarMoeda = (e) => {
  let valor = e.target.value.replace(/\D/g, ""); 
  if (valor === "") valor = "0";
  if (valor.length > 7) valor = valor.slice(0, 7);
  const numero = parseInt(valor) / 100;
  const formatado = numero.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  precoDisplay.value = formatado;
  e.target.value = formatado;
};

const salvarEvento = async () => {
  loading.value = true;
  try {
    const precoLimpo = precoDisplay.value ? precoDisplay.value.replace(/\./g, '').replace(',', '.') : '0';
    const precoFloat = parseFloat(precoLimpo);
    const lotacaoInt = parseInt(form.numeroMaximoIngressos);

    if (isNaN(precoFloat) || precoFloat < 0) return alert("Preço inválido.");
    if (isNaN(lotacaoInt) || lotacaoInt <= 0) return alert("Lotação inválida.");

    const dataFormatada = form.dataEvento.replace('T', ' ');

    const response = await api.post('/evento', {
      nome: form.nome,
      data: dataFormatada,
      localizacao: form.localizacao,
      numeroMaximoIngressos: lotacaoInt,
      preco: precoFloat
    }, { params: { acao: 'cadastro' } });

    if (response.data.status === 'SUCESSO') {
      alert('Evento criado com sucesso!');
      router.push('/dashboard');
    } else {
      alert(response.data.mensagem || 'Erro ao criar evento.');
    }
  } catch (error) {
    alert('Erro de conexão com o servidor.');
  } finally {
    loading.value = false;
  }
};

const cancelar = () => router.back();
</script>