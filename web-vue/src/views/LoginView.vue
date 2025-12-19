<template>
  <div class="auth-container">
    <div class="card">
      <h2>🔐 Acesso ao TicketPro</h2>
      
      <form @submit.prevent="fazerLogin">
        <div class="form-group">
          <label>E-mail</label>
          <input 
            type="email" 
            v-model="email" 
            placeholder="seu@email.com" 
            required 
            maxlength="100"
            @input="email = email.replace(/[^a-zA-Z0-9@._-]/g, '')"
          />
          <small class="counter">{{ email.length }}/100</small>
        </div>

        <div class="form-group">
          <label>Senha</label>
          <input 
            type="password" 
            v-model="senha" 
            placeholder="Sua senha" 
            required 
            maxlength="50"
            @input="senha = senha.replace(/[^a-zA-Z0-9]/g, '')"
          />
          <small class="counter">{{ senha.length }}/50</small>
        </div>

        <button type="submit" :disabled="loading" class="btn-login">
          {{ loading ? 'Entrando...' : 'Entrar' }}
        </button>
      </form>
      
      <p class="link-cadastro">
        Não tem conta? <router-link to="/cadastro">Cadastre-se aqui</router-link>
      </p>

      <div v-if="erro" class="alert error">{{ erro }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';
import { store } from '../store';

const router = useRouter();
const email = ref('');
const senha = ref('');
const erro = ref('');
const loading = ref(false);

const fazerLogin = async () => {
  loading.value = true;
  erro.value = '';

  try {
    const response = await api.post('/usuario', {
      email: email.value,
      senha: senha.value
    }, {
      params: { acao: 'login' }
    });

    if (response.data.status === 'SUCESSO') {
      store.fazerLogin(response.data.usuario);
      router.push('/dashboard');
    } else {
      erro.value = response.data.mensagem || 'Login falhou.';
    }
  } catch (e) {
    erro.value = 'Erro de conexão com o servidor.';
    console.error(e);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.auth-container { display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #2c3e50; }
.card { background: white; padding: 2.5rem; border-radius: 10px; width: 100%; max-width: 400px; box-shadow: 0 4px 15px rgba(0,0,0,0.2); }
h2 { text-align: center; color: #333; margin-bottom: 20px; }
.form-group { margin-bottom: 15px; }
label { display: block; margin-bottom: 5px; font-weight: bold; color: #555; }
input { width: 100%; padding: 12px; border: 1px solid #ccc; border-radius: 5px; font-size: 1rem; box-sizing: border-box; background: white; color: #333; }
input:focus { border-color: #3498db; outline: none; }
.counter { display: block; text-align: right; font-size: 0.75rem; color: #999; margin-top: 4px; }
.btn-login { width: 100%; padding: 12px; background: #3498db; color: white; border: none; border-radius: 5px; font-size: 1rem; cursor: pointer; font-weight: bold; transition: background 0.2s; }
.btn-login:hover:not(:disabled) { background: #2980b9; }
.btn-login:disabled { background: #bdc3c7; cursor: not-allowed; }
.link-cadastro { text-align: center; margin-top: 15px; font-size: 0.9rem; }
.link-cadastro a { color: #3498db; text-decoration: none; font-weight: bold; }
.alert { margin-top: 15px; padding: 10px; border-radius: 5px; text-align: center; background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
</style>