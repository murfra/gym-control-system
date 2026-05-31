const axios = require('axios');

// Example GET request using async/await
async function fetchData() {
  try {
    const response = await axios.get('https://jsonplaceholder.typicode.com/todos/1');
    console.log(response.data);
  } catch (error) {
    console.error('Error fetching data:', error.message);
  }
}

async function criarUsuario() {
  const url = 'https://typicode.com';

  // Dados que serão enviados no corpo (body) da requisição
  const dadosDoUsuario = {
    title: 'Novo Artigo',
    body: 'Conteúdo do artigo enviado via Axios POST.',
    userId: 1
  };

  try {
    // O segundo parâmetro do axios.post é o corpo da requisição
    const resposta = await axios.post(url, dadosDoUsuario);

    console.log('Status da resposta:', resposta.status); // Deve retornar 201 (Criado)
    console.log('Dados recebidos do servidor:', resposta.data);
  } catch (error) {
    console.error('Erro ao fazer o POST:', error.message);
  }
}

async function atualizarUsuario() {
  // Atualizando o post com ID 1
  const url = 'https://typicode.com';

  const dadosAtualizados = {
    id: 1,
    title: 'Título Atualizado',
    body: 'Este conteúdo foi modificado usando o método PUT.',
    userId: 1
  };

  try {
    const resposta = await axios.put(url, dadosAtualizados);

    console.log('Status do PUT:', resposta.status); // Deve retornar 200 (OK)
    console.log('Dados atualizados:', resposta.data);
  } catch (error) {
    console.error('Erro no PUT:', error.message);
  }
}

async function deletarUsuario() {
  // Deletando o post com ID 1
  const url = 'https://typicode.com';

  try {
    const resposta = await axios.delete(url);

    console.log('Status do DELETE:', resposta.status); // Deve retornar 200 ou 204
    console.log('O recurso foi deletado com sucesso!');
  } catch (error) {
    console.error('Erro no DELETE:', error.message);
  }
}

fetchData();
criarUsuario();
atualizarUsuario();
deletarUsuario();

