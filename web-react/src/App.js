import React, {useEffect, useState} from 'react';
import './App.css';

function App() {
  const [usuarios, setUsuarios] = useState([]);

  useEffect(() => {
    fetch('http://localhost:8080/users')
      .then(response => response.json())
      .then(data => {
        console.log("Dados recebidos: ", data);
        setUsuarios(data);
      })
      .catch(error => console.error('Erro ao buscar usuários: ', error));
  }, []);
  
  return (
    <div className="App">
      <header className="App-header">
        <h1>TicketPro Users</h1>
          {usuarios.length === 0 ? (
            <p>Carregando usuários ou sem conexão com a API...</p>
          ) : (
            <div className="user-list">
              {usuarios.map((usuario) => (
                <div key={usuario.id} className="user-card">
                  <h3>{usuario.nome}</h3>
                  <p>{usuario.email}</p>
                </div>
              ))}
            </div>
          )}
      </header>
    </div>
  );
}

export default App;