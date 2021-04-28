# Floricultura microservices parent
Microserviços de uma floricultura feitos utilizando Spring Cloud e suas tecnologias.

## Microservices
- **LOJA:** realiza a compra se comunicando com os demais serviços;
- **FORNECDOR:** CRUD produtos, CRUD pedidos, CRUD fornecedores;

## Service Registry - Spring Eureka

- Para habilitar o serviço com Eureka como registry, é necessário adicionar a anotação `@EnableEurekaServer` e importar a dependência **eureka-server**.

- Para **registrar** um client no Eureka, basta importar a dependência **eureka-client** e adicionar as **[propriedades](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/fornecedor/src/main/resources/application.yml)** de seu registro;

## Load Balancer - Spring CLoud

- **Ribbon:** lib do SpringCloud de **Client Side Load Balancing** onde o cliente decidirá qual instância do serviço receberá a requisição. RestTemplate e FeignClient possuem suporte.

- **RestTemplate:** podemos resolver o nome dos host dos clientes registrados no Eureka com a anotação `@LoadBalancer` habilitando, também, o **C.S. Load Balancing** nas requisições.

- **FeignClient:** permite consumir as aplicações registradas no Eureka com Load Balancer. Deve-se adicionar `@EnableFeignClients` na classe main, criar interfaces com os métodos e anotações que mapearão as requisições e importar a dependência **openfeign**.

## Tracing distribuído - Spring Sleuth

- **Spring Sleuth:** criará um **Trace ID** que identificará a transação na comunicação entre os serviços. Para adicioná-lo aos logs, utilizaremos modificaremos o sufixo enviado ao  papertrail:
    - `<suffixPattern>%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}</suffixPattern>`

- **Papertrail:** permite **centralizar logs** informando suas propriedades de conexão em **[logback.xml](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/fornecedor/src/main/resources/logback.xml)**. 

