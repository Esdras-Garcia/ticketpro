<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import { ref, watchEffect } from 'vue'

const router = useRouter()
const logado = ref(false)

watchEffect(() => {
  logado.value = !!localStorage.getItem('usuario_ticketpro')
})

const logout = () => {
  localStorage.removeItem('usuario_ticketpro')
  logado.value = false
  router.push('/')
}
</script>

<template>
  <header v-if="logado">
    <nav>
      <div class="logo">🎟️ TicketPro</div>
      <div class="links">
        <router-link to="/dashboard">Eventos</router-link>
        <router-link to="/meus-pedidos">Meus Pedidos</router-link>
        <button @click="logout" class="btn-logout">Sair</button>
      </div>
    </nav>
  </header>

  <RouterView />
</template>

<style scoped>
header { background: #333; color: white; padding: 1rem; }
nav { display: flex; justify-content: space-between; align-items: center; }
.links a { color: white; text-decoration: none; margin-right: 15px; font-weight: bold; }
.links a.router-link-active { color: #42b883; }
.btn-logout { background: transparent; border: 1px solid white; color: white; cursor: pointer; padding: 5px 10px; border-radius: 4px; }
.btn-logout:hover { background: white; color: #333; }
</style>