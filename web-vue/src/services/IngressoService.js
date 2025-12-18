import api from './api';

const ENDPOINT = '/ingresso';

export default {
    gerarLote(dados) {
        return api.post(ENDPOINT, dados, {
        params: { acao: 'gerarLote' }
        });
    },

    listarPorEvento(eventoId) {
        return api.get(ENDPOINT, {
        params: { 
            acao: 'listar',
            eventoId: eventoId
        }
        });
    },

    limparEstoque(eventoId) {
        return api.post(ENDPOINT, {}, {
            params: {
                acao: 'limparEstoque',
                eventoId: eventoId
            }
        });
    }
};