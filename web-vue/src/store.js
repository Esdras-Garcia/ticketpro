import { reactive } from 'vue';

const usuarioSalvo = localStorage.getItem('usuario_ticketpro');

export const store = reactive({
  logado: !!usuarioSalvo,
  usuario: usuarioSalvo ? JSON.parse(usuarioSalvo) : {},

  fazerLogin(dadosUsuario) {
    this.logado = true;
    this.usuario = dadosUsuario;
    localStorage.setItem('usuario_ticketpro', JSON.stringify(dadosUsuario));
  },

  fazerLogout() {
    this.logado = false;
    this.usuario = {};
    localStorage.removeItem('usuario_ticketpro');
  }
});