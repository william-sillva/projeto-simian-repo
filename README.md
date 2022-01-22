# projeto-simian-repo
API responsável por validar uma sequência de DNA e identificar corretamente símios e humanos.


1.Sincronizar o projeto
    - git clone https://github.com/william-sillva/projeto-simian-repo.git

2. Acessar o diretório do projeto e instalar as dependências maven, se necessário.
    - mvn install

3. O sistema está ligado a um banco de dados Postgre. Pode ser instalado no host local ou utilizando uma imagem "docker pull postgres";
    - Configuração:
      - url = jdbc:postgresql://localhost:5432/postgres
      - schema = simian_dna
      - username = postgres
      - password = postgres
   
4. O sistema está utilizando o flyway que executará o script V1__init.sql. Este script criará a tabela responsável por receber os DNA's verificados.

5. Iniciar o Springboot
   - mvn spring-boot:run

6. O sistema conta com o swagger. Este pode ser acessado pela url:
    - "/swagger-ui/index.html"

Execução
    - endpoint SIMIAN -> "/api/dnas/simian";
    - endpoint STATS -> "/api/dnas/stats";

