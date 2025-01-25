// Para iniciar navegue até o dir data e rode docker-compose up -d
// Apos isso inicie os servicos

docker-compose down
docker-compose up -d
docker container prune // remove containers antigos e parados

8081 - filter // ok já pronto.
8033 - item // ok, já pronto.
8034 - user // ok, já pronto.
8035 - project // ok, já pronto.
8036 - place // Adicionar verificação se a obra existe antes de criar a bacia.
8037 - place_item // ok, já pronto.
8038 - measurement // ok, já pronto
8039 - measurement_place_item // ok, já pronto

//Criar get itens, bacias, obras, medicoes, medicoes bacias itens com base no email cadastrado.
//Concluir implementação dos testes unitários
//Realizar a documentação com swagger
//Implementar testes de integração e carga.
//Implementar frontend

