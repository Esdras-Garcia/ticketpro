import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import CadastroView from '../views/CadastroView.vue'
import DashboardView from '../views/DashboardView.vue'
import PerfilView from '../views/PerfilView.vue' 
import EventoCadastroView from '../views/EventoCadastroView.vue'
import PedidosView from '../views/PedidosView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'login', component: LoginView },
    { path: '/cadastro', name: 'cadastro', component: CadastroView },
    { path: '/dashboard', name: 'dashboard', component: DashboardView },
    { path: '/meus-pedidos', name: 'pedidos', component: PedidosView },
    { path: '/perfil', name: 'perfil', component: PerfilView },
    { path: '/evento/novo', name: 'novo-evento', component: EventoCadastroView },
    { path: '/meus-pedidos', name: 'pedidos', component: PedidosView }
  ]
})

router.beforeEach((to, from, next) => {
  const publicPages = ['/', '/cadastro'];
  const authRequired = !publicPages.includes(to.path);
  const loggedIn = localStorage.getItem('usuario_ticketpro');

  if (authRequired && !loggedIn) {
    next('/');
  } else {
    next();
  }
});

export default router