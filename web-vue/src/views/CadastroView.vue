<template>
  <div class="auth-container">
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
            minlength="3"
            maxlength="30"
            @input="form.nome = form.nome.replace(/[^a-zA-ZÀ-ÿ\s]/g, '')"
            :class="{ 'error-input': errors.nome }"
          />
          <div class="info-row">
             <span v-if="errors.nome" class="error-msg">{{ errors.nome }}</span>
             <small class="counter">{{ form.nome.length }}/30</small>
          </div>
        </div>

        <div class="form-group">
          <label for="email">E-mail</label>
          <input 
            type="email" 
            id="email" 
            v-model="form.email" 
            placeholder="email@exemplo.com" 
            required
            maxlength="30"
            @input="form.email = form.email.replace(/[^a-zA-Z0-9@._-]/g, '')"
          />
          <small class="counter">{{ form.email.length }}/30</small>
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
            maxlength="20"
            @input="form.senha = form.senha.replace(/[^a-zA-Z0-9]/g, '')"
          />
          <div class="info-row">
            <small style="font-size: 0.75rem; color: #777;">Apenas letras e números.</small>
            <small class="counter">{{ form.senha.length }}/20</small>
          </div>
        </div>

        <div class="actions">
          <button type="button" class="btn-cancel" @click="cancelar">Cancelar</button>
          <button type="submit" class="btn-primary" :disabled="isLoading">
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
const form = reactive({ nome: '', email: '', senha: '' });
const isLoading = ref(false);
const errorMessage = ref('');
const errors = reactive({});

const validarFormulario = () => {
  errors.nome = '';
  if (form.nome.length < 3) {
    errors.nome = 'O nome deve ter pelo menos 3 caracteres.';
    return false;
  }
  return true;
};

const submitForm = async () => {
  if (!validarFormulario()) return;
  isLoading.value = true;
  errorMessage.value = '';
  try {
    const response = await api.post('/usuario', form, { params: { acao: 'cadastro' } });
    if (response.data.status === 'SUCESSO') {
        alert('Cadastro realizado com sucesso!');
        router.push('/'); 
    } else {
        errorMessage.value = response.data.mensagem || 'Erro ao realizar cadastro.';
    }
  } catch (error) {
    errorMessage.value = 'Erro ao conectar com o servidor.';
  } finally {
    isLoading.value = false;
  }
};

const cancelar = () => router.back();
</script>

<style scoped>
.auth-container { display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #2c3e50; }
.card { background: white; padding: 2.5rem; border-radius: 10px; width: 100%; max-width: 450px; box-shadow: 0 4px 15px rgba(0,0,0,0.2); }
h2 { text-align: center; color: #333; margin-bottom: 20px; }
.form-group { margin-bottom: 15px; }
label { display: block; margin-bottom: 5px; font-weight: bold; color: #555; }
input { width: 100%; padding: 12px; border: 1px solid #ccc; border-radius: 5px; font-size: 1rem; box-sizing: border-box; background: white; color: #333; }
input:focus { border-color: #3498db; outline: none; }
.info-row { display: flex; justify-content: space-between; margin-top: 4px; }
.counter { display: block; text-align: right; font-size: 0.75rem; color: #999; margin-left: auto; }
.error-input { border-color: #e74c3c; }
.error-msg { color: #e74c3c; font-size: 0.85rem; }
.actions { display: flex; gap: 10px; margin-top: 20px; }
button { flex: 1; padding: 12px; border: none; border-radius: 5px; font-size: 1rem; cursor: pointer; font-weight: bold; transition: background 0.2s; }
.btn-primary { background: #3498db; color: white; }
.btn-primary:hover:not(:disabled) { background: #2980b9; }
.btn-primary:disabled { background: #bdc3c7; cursor: not-allowed; }
.btn-cancel { background: #95a5a6; color: white; }
.btn-cancel:hover { background: #7f8c8d; }
.alert.error { margin-top: 15px; padding: 10px; border-radius: 5px; text-align: center; background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
</style>