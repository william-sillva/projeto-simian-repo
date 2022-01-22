# Instruções - Projeto Simian DNA
API responsável por validar uma sequência de DNA e identificar corretamente símios e humanos.
# _____________________________

1. Sincronizar o projeto
    - git clone https://github.com/william-sillva/projeto-simian-repo.git

2. Acessar o diretório do projeto e instalar as dependências maven, se necessário.
    - mvn install

3. O sistema está ligado a um banco de dados Postgre. Pode ser instalado no host local ou utilizando uma imagem Docker "docker pull postgres";
    - Configuração:
      - url = jdbc:postgresql://localhost:5432/postgres
      - schema = simian_dna
      - username = postgres
      - password = postgres
   
4. O sistema está utilizando o flyway que executará os scripts dentro da pasta "resources.db.migration". Estes scripts são responsáveis por criar e atualizar as estruturas dentro do dataBase.

5. Iniciar o Springboot
   - mvn spring-boot:run

6. O sistema conta com o swagger. Este pode ser acessado pela url:
    - http://localhost:8080/swagger-ui/index.html
    - https://simian-api.herokuapp.com/swagger-ui/index.html

Execução ENDPOINT
   - LOCALHOST
     - http://localhost:8080/api/dnas/simian
     - http://localhost:8080/api/dnas/stats
   - PRODUCAO
     - https://simian-api.herokuapp.com/api/dnas/simian
     - https://simian-api.herokuapp.com/api/dnas/stats

