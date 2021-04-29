# Floricultura microservices parent
> Microserviços de uma floricultura feitos utilizando **Spring Cloud** e suas tecnologias.
<br>

## Microservices
- **EUREKA-SERVER**: atua como serviço de registro das nossas aplicações;
- **LOJA:** realiza a compra se comunicando com os demais serviços. Possui seu proprio database;
- **FORNECDOR:** CRUD produtos, CRUD pedidos, CRUD fornecedores. Possui seu proprio database;
<br>

## Service Registry - Spring Eureka

- Para habilitar o projeto(*eureka-server*) como **registry**, é necessário importar o starter **eureka-server** e adicionar a anotação `@EnableEurekaServer`.

- **Eureka Client** nos permite registrar um client(*loja* e *fornecedor*) no Eureka adicionando as propriedades referentes ao seu registro em seu **[application.yml](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/fornecedor/src/main/resources/application.yml)**.
<br>

## Load Balancer - Ribbon

- **Ribbon:** lib do SpringCloud de **Client Side Load Balancing** onde o cliente decidirá qual instância do serviço receberá a requisição. RestTemplate e FeignClient possuem suporte.

- **RestTemplate:** podemos resolver o nome dos host dos clientes registrados no Eureka com a anotação `@LoadBalancer` o que habilita, também, o **C.S. Load Balancing** nas requisições.

- **FeignClient:** permite consumir as aplicações registradas no Eureka com Load Balancer. Deve-se adicionar `@EnableFeignClients` na classe main e criar interfaces([example](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/loja/src/main/java/br/com/alura/microservice/loja/client/FornecedorClient.java)) com os métodos e anotações que mapearão as requisições.
<br>

## Tracing distribuído - Spring Sleuth

- **Spring Sleuth:** criará um **Trace ID** que identificará a transação na comunicação entre os serviços. Para mostrá-los nos logs, basta modificar os patterns de **[logback.xml](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/fornecedor/src/main/resources/logback.xml)** onde for desejado. Exemplo:
```
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %clr(${LOG_LEVEL_PATTERN}) %clr(${PID:-}){magenta} [%thread] %clr(%logger{39}){cyan} - %msg%n</pattern>
```

- **Papertrail:** permite **centralizar logs** informando suas propriedades de conexão em **[logback.xml](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/fornecedor/src/main/resources/logback.xml)**. 
<br>

## Circuit Breaker - Hystrix

- **Hystrixs:** utilizaremos `@EnableCircuitBreaker` para habilitá-lo na app e `@HystrixCommand` nos métodos para que a lib o gerencie e aplique suas funcionalidades. O Hystrix passará a gerenciar as threads.
    - Um **fallback** pode ser definido para um método que será chamado quando o Circuit Breaker ocorrer: `@HystrixCommand(fallbackMethod="realizaCompraFallback")`.
    - Se houver **muitas falhas**, o Hystrix possui inteligencia de chamar o fallback por padrão durante um tempo.
    - **Bulkhead:** para evitar que, ao haver uma sobrecarga de chamadas para uma operação, outra operação fique sem threads disponíveis, criaremos um **pool de thread** separado para cada metodo: `@HystrixCommand(threadPoolKey="key")`.
