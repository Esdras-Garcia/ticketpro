<template>
  <div class="cadastro-container">
    <div class="card">
      <h2>Novo Cadastro</h2>
      
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="nome">Nome Completo</label>
          <input 
            type="text" 
            id="nome" 
            v-model="form.nome" 
            placeholder="Digite o nome" 
            required
            :class="{ 'error-input': errors.nome }"
          />
          <span v-if="errors.nome" class="error-msg">{{ errors.nome }}</span>
        </div>

        <div class="form-group">
          <label for="email">E-mail</label>
          <input 
            type="email" 
            id="email" 
            v-model="form.email" 
            placeholder="email@exemplo.com" 
            required
          />
        </div>

        <div class="form-group">
          <label for="senha">Senha</label>
          <input 
            type="password" 
            id="senha" 
            v-model="form.senha" 
            placeholder="********" 
            required
            minlength="6"
          />
        </div>

        <div class="actions">
          <button type="button" class="btn-cancel" @click="cancelar">Cancelar</button>
          <button type="submit" class="btn-save" :disabled="isLoading">
            {{ isLoading ? 'Salvando...' : 'Salvar Cadastro' }}
          </button>
        </div>
      </form>

      <div v-if="errorMessage" class="alert error">
        {{ errorMessage }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();

const form = reactive({
  nome: '',
  email: '',
  senha: ''
});

const isLoading = ref(false);
const errorMessage = ref('');
const errors = reactive({});

const validarFormulario = () => {
  errors.nome = '';
  let isValid = true;

  if (form.nome.length < 3) {
    errors.nome = 'O nome deve ter pelo menos 3 caracteres.';
    isValid = false;
  }

  return isValid;
};

const submitForm = async () => {
  if (!validarFormulario()) return;

  isLoading.value = true;
  errorMessage.value = '';

  try {
    const response = await api.post('/usuarios', form);

    console.log('Resposta do servidor:', response.data);
    alert('Cadastro realizado com sucesso!');
    
    router.push('/'); 

  } catch (error) {
    console.error('Erro na requisição:', error);
    if (error.response) {
       errorMessage.value = `Erro: ${error.response.data.message || 'Falha no servidor'}`;
    } else if (error.request) {
       errorMessage.value = 'Sem resposta do servidor. Verifique se o backend está rodando.';
    } else {
       errorMessage.value = 'Erro ao configurar a requisição.';
    }
  } finally {
    isLoading.value = false;
  }
};

const cancelar = () => {
  router.back();
};
</script>

<style scoped>
.cadastro-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
  background-color: #f4f6f8;
}

.card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
}

h2 {
  margin-bottom: 1.5rem;
  color: #333;
  text-align: center;
}

.form-group {
  margin-bottom: 1.2rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #555;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

input:focus {
  border-color: #4CAF50;
  outline: none;
}

.error-input {
  border-color: #e74c3c;
}

.error-msg {
  color: #e74c3c;
  font-size: 0.875rem;
  margin-top: 0.25rem;
  display: block;
}

.actions {
  display: flex;
  justify-content: space-between;
  margin-top: 2rem;
  gap: 1rem;
}

button {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-save {
  background-color: #4CAF50;
  color: white;
}

.btn-save:hover:not(:disabled) {
  background-color: #45a049;
}

.btn-save:disabled {
  background-color: #a5d6a7;
  cursor: not-allowed;
}

.btn-cancel {
  background-color: #e0e0e0;
  color: #333;
}

.btn-cancel:hover {
  background-color: #d5d5d5;
}

.alert.error {
  margin-top: 1rem;
  padding: 0.75rem;
  background-color: #fdecea;
  color: #e74c3c;
  border: 1px solid #fadbd8;
  border-radius: 4px;
  text-align: center;
}
</style>