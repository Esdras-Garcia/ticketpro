<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

const router = useRouter()
const email = ref('')
const senha = ref('')
const erro = ref('')

const fazerLogin = async () => {
  try {
    const res = await api.post('/usuario', {
      email: email.value, 
      senha: senha.value 
    }, {
      params: { acao: 'login' }
    });
    
    const data = res.data;

    if (data.status === 'SUCESSO') {
      localStorage.setItem('usuario_ticketpro', JSON.stringify(data.usuario));
      router.push('/dashboard');
    } else {
      erro.value = data.mensagem;
    }
  } catch (e) {
    erro.value = "Erro de conexão com o servidor Java.";
  }
}
</script>

<template>
  <div class="login-container">
    <h2>Entrar no TicketPro</h2>
    <div class="form">
      <input v-model="email" placeholder="E-mail" type="email" />
      <input v-model="senha" placeholder="Senha" type="password" />
      <button @click="fazerLogin">ENTRAR</button>
      <p v-if="erro" class="error">{{ erro }}</p>
      <p>Não tem conta? <router-link to="/cadastro">Cadastre-se</router-link></p>
    </div>
  </div>
</template>

<style scoped>
.login-container { max-width: 300px; margin: 50px auto; text-align: center; }
input { display: block; width: 100%; margin: 10px 0; padding: 10px; }
button { width: 100%; padding: 10px; background: #42b883; color: white; border: none; cursor: pointer; }
.error { color: red; margin-top: 10px; }
</style>